/* eslint-disable */
import * as firebase from "firebase-admin";
import * as functions from "firebase-functions";
const http = require('http');

firebase.initializeApp();
const firestore = firebase.firestore();

export const addMessage = functions.https.onRequest(async (req, res) => {
  //const width: number = parseInt(req.query.width as string);
  var count = 0;
  var start = Date.now();
  var end = start + 10 * 1000;
  //  for (var i = 0; Date.now() < end; ++i ) {
  for (var i = 0; i < 1; ++i) {
    var result = await report(`${i}`)
    if (result != null) count++;
  }
  res.json({ "count": `${count}`, "start": start, "end": end, "svrtime": firebase.firestore.FieldValue.serverTimestamp() });
});

function report(id: String) {
  return firestore.collection('messages').add(
    {
      "count": id,
      "now": `${Date()}`,
      "time": `${Date.now()}`,
    }
  );
}

async function getAsync(url: string): Promise<string> {
  http.get(url, (r: any) => {
    r.on('data', (d: any) => {
      return d;
    });
  }).on('error', (e: any) => {
    //return null;
    return "X";
  });
  return "XXX";
}

export const runLauncher = functions.https.onRequest(async (req, res) => {
  const devId = req.query.id as string;
  const devNum = parseInt(req.query.n as string);

  const x = await getAsync(`http://localhost:5001/stress1/us-central1/runAgent/?id=${devId}&n=${devNum}`);
  if (x == null) res.json({ "res": "error" });
  else res.json({ "res": `${x}` })

  /*  http.get(`http://localhost:5001/stress1/us-central1/runAgent/?id=${devId}`, (r: any) => {
      r.on('data', (d: any) => {
        res.json({ "devId": `${devId}`, "num": `${devNum}`, "res": `on()${d}`, "start": `${Date.now()}`, "msg": `${Date()}` });
      });
    }).on('error', (e: any) => {
      console.error(e);
      res.json({ "err": `${e}` });
    });
  */
});


// 最速でn回書き込む
// .../runAgent/?id=<devId>&n=<writes>
export const runAgent = functions.https.onRequest(async (req, res) => {
  const devId = req.query.id as string
  const num = parseInt(req.query.n as string)

  var c = 0;
  for (var i = 0; i < num; ++i) {
    const log = {
      "id": devId, "count": i, "now": Date(), "time": Date.now(),
      "svrtime": firebase.firestore.FieldValue.serverTimestamp()
    }
    const r = await firestore.collection('messages').add(log)
    if (r != null) ++c
  }

  res.json({ "devId": `${devId}`, "count": c, "start": `${Date.now()}`, "msg": `${Date()}` })
})

// 指定時刻tまで待ち同時にn回Write
// .../startAt/?id=<devId>&n=<writes>&et=<entryTime>&st=<startTime>

export const startAt = functions.https.onRequest(async (req, res) => {
  const sleep = (msec: number) => new Promise(resolve => setTimeout(resolve, msec));
  async function addRecord(id: string) {
    const log = {
      "id": id, "now": Date(), "time": Date.now(),
      "svrtime": firebase.firestore.FieldValue.serverTimestamp()
    }
    await firestore.collection('messages').add(log)
    return `${id}, ${Date.now()}`
  }
  const devId = req.query.id as string
  const entryTime = parseInt(req.query.et as string)
  const calledTime = Date.now()
  const startTime = parseInt(req.query.st as string)
  const nMsg = parseInt(req.query.n as string)

  await sleep(startTime - Date.now())
  var proms = Array<Promise<string>>();
  for (var i = 0; i < nMsg; ++i) {
    //var r = firestore.collection('messages').add({ "id": `${devId}-${i}`, "now": Date(), "time": Date.now(), })
    var r = addRecord(`${devId},${i}`)
    proms.push(r)
  }
  var rs = (await Promise.all(proms))
  res.json({ "devId": `${devId}`, "et": entryTime, "ct": calledTime, "st": startTime, "end": `${Date.now()}`, "res": rs })
})

// countup/?id=<devid>&q=<queryDocId>
export const countUpAgent = functions.https.onRequest(async (req, res) => {
  //const devId = req.query.id as string;
  const queryDocId = req.query.q as string;

  const r = await waitUpdateQuery(queryDocId);

  //setTimeout(() => {
  //  res.json({ "devId": devId, "query": queryDocId });
  //}, 50 * 1000);

  res.json({ "aaa": `${r}`, "bbb": queryDocId });
});

async function waitUpdateQuery(queryDocId: string): Promise<FirebaseFirestore.DocumentSnapshot<FirebaseFirestore.DocumentData> | void> {
  const unsub = firestore.collection('query').doc(queryDocId).onSnapshot(
    docSnapshot => {
      return docSnapshot;
    },
    err => {
      return null;
    },
  );
  unsub();
};
