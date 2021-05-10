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
      return d
    });
  }).on('error', (e: any) => {
    return `${e}`
  });
  return "no"
}

// startAtをnr回起動
// .../startAtLauncher/?id=<dev_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>
export const startAtLauncher = functions.https.onRequest(async (req, res) => {
  const devId = req.query.id as string
  const nr = parseInt(req.query.nr as string)
  const nm = parseInt(req.query.nm as string)
  const ts = parseInt(req.query.ts as string)

  var proms = Array<Promise<string>>()

  for (var i = 0; i < nr; ++i) {
    const r = getAsync(`${req.originalUrl}/startAt/?id=${devId},${i}&n=${nm}&ts=${ts}`)
    proms.push(r)
  }
  var rs = (await Promise.all(proms))
  res.json({ "res": `${rs}` })
})

// 指定時刻tまで待ち同時にn回Write
// .../startAt/?id=<dev_id>&n=<writes>&ts=<start_time>
export const startAt = functions.https.onRequest(async (req, res) => {
  const sleep = (msec: number) => new Promise(resolve => setTimeout(resolve, msec))
  async function addRecord(id: string) {
    const log = {
      "id": id,
      "now": Date(),
      "time": Date.now(),
      "svrtime": firebase.firestore.FieldValue.serverTimestamp()
    }
    firestore.collection('messages').add(log)
    return `${id}, ${Date.now()}`
  }
  const devId = req.query.id as string
  const startTime = parseInt(req.query.ts as string)
  const nMsg = parseInt(req.query.n as string)

  await sleep(startTime - Date.now())
  var proms = Array<Promise<string>>()
  for (var i = 0; i < nMsg; ++i) {
    var r = addRecord(`${devId},${i}`)
    proms.push(r)
  }
  var rs = (await Promise.all(proms))
  res.json({ "devid": `${devId}`, "ts": startTime, "end": `${Date.now()}`, "res": rs })
})

