/* eslint-disable */
import * as firebase from "firebase-admin"
import * as functions from "firebase-functions"
import axios, { AxiosResponse } from 'axios'

const axiosClient = axios.create({
  baseURL: 'https://us-central1-stress1.cloudfunctions.net',
  headers: { 'Content-Type': 'application/json' },
  timeout: 60000, // milliseconds
})

//const https = require('https');

/*
https.get('https://jsonplaceholder.typicode.com/users', (res) => {
  let data = [];
  const headerDate = res.headers && res.headers.date ? res.headers.date : 'no response date';
  console.log('Status Code:', res.statusCode);
  console.log('Date in Response header:', headerDate);

  res.on('data', (chunk) => {
    data.push(chunk);
  });

  res.on('end', () => {
    console.log('Response ended: ');
    const users = JSON.parse(Buffer.concat(data).toString());

    for(user of users) {
      console.log(`Got user with id: ${user.id}, name: ${user.name}`);
    }
  });
}).on('error', (err) => {
  console.log('Error: ', err.message);
});
*/

firebase.initializeApp();
const firestore = firebase.firestore();

const sleep = (msec: number) => new Promise(resolve => setTimeout(resolve, msec))
/*
async function httpGetCB(url: string, callback: (err: any, res: any) => void) {
  https.get(url, (r: any) => {
    r.on('data', (d: any) => {
      callback(null, d)
    });
  }).on('error', (e: any) => {
    callback(e, null)
  })
  await sleep(50 * 1000)
  callback("Timeout", null)
}
*/

//import * as util from 'util'
//const httpGet = util.promisify(httpGetCB)

// startAtLauncherをnr回起動
// .../startAtLauncher/?id=<launch_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>
export const startAtLauncher1 = functions.https.onRequest(async (req, res) => {
  try {
    const timeCalled = Date.now()
    const launchId = req.query.id as string
    const nr1 = parseInt(req.query.nr1 as string)
    const nr = parseInt(req.query.nr as string)
    const nm = parseInt(req.query.nm as string)
    const timeToStart = parseInt(req.query.ts as string)
    var proms = Array<Promise<AxiosResponse>>()
    for (var i = 0; i < nr1; ++i) {
      const pr = axiosClient.get(`/startAtLauncher/?id=${launchId},${i}&nr=${nr}&nm=${nm}&ts=${timeToStart}`)
      proms.push(pr)
    }
    var rs = await Promise.all(proms)
    var s = 0
    rs.forEach((e) => { s = s + (e.data.cs as number) })
    res.json({ "id": launchId, "tc": timeCalled, "ts": timeToStart, "te": Date.now(), "cr": rs.length, "cs": s })
  } catch (ex) {
    res.json({ "ex": `${ex}` })
  }
})

// startAtをnr回起動
// .../startAtLauncher/?id=<launch_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>
export const startAtLauncher = functions.https.onRequest(async (req, res) => {
  try {
    const timeCalled = Date.now()
    const launchId = req.query.id as string
    const nr = parseInt(req.query.nr as string)
    const nm = parseInt(req.query.nm as string)
    const timeToStart = parseInt(req.query.ts as string)
    var proms = Array<Promise<AxiosResponse>>()
    for (var i = 0; i < nr; ++i) {
      const pr = axiosClient.get(`/startAt/?id=${launchId},${i}&n=${nm}&ts=${timeToStart}`)
      proms.push(pr)
    }
    var rs = await Promise.all(proms)
    var s = 0
    rs.forEach((e) => { s = s + (e.data.cs as number) })
    res.json({ "id": launchId, "tc": timeCalled, "ts": timeToStart, "te": Date.now(), "cr": rs.length, "cs": s })
    //res.json({ "res": `${rs}` })
  } catch (ex) {
    res.json({ "ex": `${ex}` })
  }
})

// 指定時刻tまで待ち同時にn回Write
// .../startAt/?id=<dev_id>&n=<writes>&ts=<start_time>
export const startAt = functions.https.onRequest(async (req, res) => {
  try {
    const timeCalled = Date.now()
    const devId = req.query.id as string
    const timeToStart = parseInt(req.query.ts as string)
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
    await sleep(timeToStart - Date.now())
    var proms = Array<Promise<any>>()
    for (var i = 0; i < nMsg; ++i) {
      var r = addRecord(`${devId},${i}`)
      proms.push(r)
    }
    var rs = (await Promise.all(proms))
    res.json({ "id": `${devId}`, "tc": timeCalled, "ts": timeToStart, "te": Date.now(), "cr": rs.length, "cs": nMsg })
  } catch (e) {
    res.json({ "ex": `${e}` })
  }
})

