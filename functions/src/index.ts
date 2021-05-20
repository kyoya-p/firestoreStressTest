/* eslint-disable */
import * as firebase from "firebase-admin";
import * as functions from "firebase-functions";
const http = require('https');

firebase.initializeApp();
const firestore = firebase.firestore();

const sleep = (msec: number) => new Promise(resolve => setTimeout(resolve, msec))


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
      "time": Date.now(),
      "svrTime": firebase.firestore.FieldValue.serverTimestamp(),
    }
  );
}


async function httpGetCB(url: string, callback: (err: any, res: any) => void) {
  http.get(url, (r: any) => {
    r.on('data', (d: any) => {
      callback(null, d)
    });
  }).on('error', (e: any) => {
    callback(e, null)
  })
  await sleep(50 * 1000)
  callback("Timeout", null)
}

import * as util from 'util'
const httpGet = util.promisify(httpGetCB)

// startAtLauncherをnr回起動
// .../startAtLauncher/?id=<launch_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>
export const startAtLauncher1 = functions.https.onRequest(async (req, res) => {
  try {
    const launchId = req.query.id as string
    const nr1 = parseInt(req.query.nr1 as string)
    const nr = parseInt(req.query.nr as string)
    const nm = parseInt(req.query.nm as string)
    const timeToStart = parseInt(req.query.ts as string)
    var proms = Array<Promise<any>>()
    for (var i = 0; i < nr1; ++i) {
      const reqUrl = `https://us-central1-stress1.cloudfunctions.net/startAtLauncher/?id=${launchId},${i}&nr=${nr}&nm=${nm}&ts=${timeToStart}`
      const r = httpGet(reqUrl)
      proms.push(r)
    }
    var rs = await Promise.all(proms)
    var s = 0
    rs.reduce((e) => { s += parseInt(e["res"] as string) })
    res.json({ "id": launchId, "nr1": nr1, "nr": nr, "nm": nm, "ts": timeToStart, "te": Date.now(), "cr": rs.length, "res": s })
  } catch (ex) {
    res.json({ "res": `${ex}` })
  }
})

// startAtをnr回起動
// .../startAtLauncher/?id=<launch_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>
export const startAtLauncher = functions.https.onRequest(async (req, res) => {
  try {
    const launchId = req.query.id as string
    const nr = parseInt(req.query.nr as string)
    const nm = parseInt(req.query.nm as string)
    const timeToStart = parseInt(req.query.ts as string)
    var proms = Array<Promise<any>>()
    for (var i = 0; i < nr; ++i) {
      const reqUrl = `https://us-central1-stress1.cloudfunctions.net/startAt/?id=${launchId},${i}&n=${nm}&ts=${timeToStart}`
      const r = httpGet(reqUrl)
      proms.push(r)
    }
    var rs = await Promise.all(proms)
    var s = 0
    rs.reduce((e) => { s += parseInt(e["res"] as string) })
    res.json({ "id": launchId, "nr": nr, "nm": nm, "ts": timeToStart, "te": Date.now(), "cr": rs.length, "res": s })
    //res.json({ "res": `${rs}` })
  } catch (ex) {
    res.json({ "res": `${ex}` })
  }
})

// 指定時刻tまで待ち同時にn回Write
// .../startAt/?id=<dev_id>&n=<writes>&ts=<start_time>
export const startAt = functions.https.onRequest(async (req, res) => {
  try {
    const devId = req.query.id as string
    const startTime = parseInt(req.query.ts as string)
    const nMsg = parseInt(req.query.n as string)
    async function addRecord(id: string) {
      const log = {
        "id": id,
        "now": Date(),
        "time": Date.now(),
        "svrtime": firebase.firestore.FieldValue.serverTimestamp()
      }
      await firestore.collection('messages').add(log)
      return `${id}, ${Date.now()}`
    }
    await sleep(startTime - Date.now())
    var proms = Array<Promise<string>>()
    for (var i = 0; i < nMsg; ++i) {
      var r = addRecord(`${devId},${i}`)
      proms.push(r)
    }
    var rs = (await Promise.all(proms))
    res.json({ "id": `${devId}`, "ts": startTime, "te": Date.now(), "cs": rs.length, "res": nMsg })
  } catch (e) {
    res.json({ "res": `${e}` })
  }
})

