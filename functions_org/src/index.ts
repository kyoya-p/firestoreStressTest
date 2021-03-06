/* eslint-disable */

import * as functions from "firebase-functions"
import axios, { AxiosResponse } from 'axios'
import { RuntimeOptions } from "firebase-functions"

import { getAuth, onAuthStateChanged } from "firebase/auth";

const auth = getAuth(firebaseApp);
onAuthStateChanged(auth, user => {
  // Check for user status
});

const firebaseConfig = {
  
};

// Initialize Firebase
firebase.default.initializeApp(firebaseConfig);


const runtimeOpts = {
  timeoutSeconds: 540,
  //memory: '512MB'
  memory: '1GB'
  //memory: '2GB'
} as RuntimeOptions
const region = "asia-northeast2"

const axiosClient = axios.create({
  baseURL: `https://${region}-stress1.cloudfunctions.net`,
  headers: { 'Content-Type': 'application/json' },
  timeout: 300 * 1000, // milliseconds
})

//firebase.initializeApp();
//const firestore = firebase.firestore();

const sleep = (msec: number) => new Promise(resolve => setTimeout(resolve, msec))

// startAtLauncherをnr回起動
// .../startAtLauncher/?id=<launch_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>
export const startAtLauncher1 = functions
  .runWith(runtimeOpts)
  .region(region)
  .https.onRequest(async (req, res) => {
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
      rs.forEach((e) => {
        if (e.data.ex != null) {
          res.json({ "ex": `${e.data.ex}` })
        } else {
          s = s + (e.data.cs as number)
        }
      })
      res.json({ "id": launchId, "tc": timeCalled, "ts": timeToStart, "te": Date.now(), "cr": rs.length, "cs": s })
    } catch (ex) {
      res.json({ "ex": `${ex}` })
    }
  })


// startAtをnr回起動
// .../startAtLauncher/?id=<launch_id_prefix>&nr=<num_of_req>&nm=<num_of_msg>&ts=<start_time>
export const startAtLauncher = functions
  .runWith(runtimeOpts)
  .region(region)
  .https.onRequest(async (req, res) => {
    var proms = Array<Promise<AxiosResponse>>()
    try {
      const timeCalled = Date.now()
      const launchId = req.query.id as string
      const nr = parseInt(req.query.nr as string)
      const nm = parseInt(req.query.nm as string)
      const timeToStart = parseInt(req.query.ts as string)
      for (var i = 0; i < nr; ++i) {
        const r = Math.floor(Math.random() * round)
        const p = axiosClient.get(`/startAt${(i + r) % round}/?id=${launchId},${i}&n=${nm}&ts=${timeToStart}`)
        //const p = axiosClient.get(`/startAt0/?id=${launchId},${i}&n=${nm}&ts=${timeToStart}`)
        proms.push(p)
      }
      const rs = await Promise.all(proms)
      res.json({ "id": launchId, "tc": timeCalled, "ts": timeToStart, "te": Date.now(), "cr": rs.length, "cs": -1 })
    } catch (ex) {
      res.json({ "ex": `${ex}` })
      console.error(`${ex}`)
      console.error(`Proms.length=${proms.length}`)
    }
  })


// 最高負荷(同期write) Max 50sec
// .../loadMaker/?id=<launch_id>&fn=<func_round>&nr=<num_of_req>&tc=<called_time>
export const loadMakerFuncs = functions
  .runWith(runtimeOpts)
  .region(region)
  .https.onRequest(async (req, res) => {
    var proms = Array<Promise<AxiosResponse>>()
    try {
      const timeCalled = Date.now()
      const id = req.query.id as string
      const nr = parseInt(req.query.nr as string)
      const fn = parseInt(req.query.fn as string || "1")
      const tc = req.query.tc as string || "no"
      for (var i = 0; i < nr; ++i) {
        await sleep(0)
        const p = axiosClient.get(`/startAt${i%fn}/?id=${id},${i}&n=1&ts=0&tc=loadMakerFunc,${Date.now()},${tc}`)
        proms.push(p)
      }
      const rs = await Promise.all(proms)
      res.json({ "id": id, "tc": timeCalled, "te": Date.now(), "cr": rs.length })
    } catch (ex) {
      res.json({ "ex": `${ex}` })
      console.error(`${ex}`)
      console.error(`Proms.length=${proms.length}`)
    }
  })

const round = 20

export const target0 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target1 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target2 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target3 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target4 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target5 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target6 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target7 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target8 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target9 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target10 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target11 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target12 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target13 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target14 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target15 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target16 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target17 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target18 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)
export const target19 = functions.runWith(runtimeOpts).region(region).https.onRequest(target)

// .../targrt/?id=<dev_id>&tc=<called_time>
async function target(req: functions.https.Request, res: functions.Response) {
  try {
    const timeCalled = Date.now()
    const id = req.query.id as string
    const tCall = parseInt(req.query.tc as string)
    /*const tCall = req.query.tc || "no"
    async function addRecord(id: string) {
      const log = {
        "id": id,
        "now": Date(),
        "time": Date.now(),
        "svrtime": firebase.firestore.FieldValue.serverTimestamp(),
        "func": tCall,
      }
      await firestore.collection('messages').add(log).catch((ex) => console.error(ex))
        .then(() => { console.log({ "success": Date.now() }) })
        .catch((e) => { console.error({ "error": e }) })

      return `${id}, ${Date.now()}`
    }
    await sleep(timeToStart - Date.now())
    var proms = Array<Promise<any>>()
    for (var i = 0; i < nMsg; ++i) {
      proms.push(addRecord(`${id},${i}`))
    }
    var rs = await Promise.all(proms)
    */

    firebase.default.analytics().logEvent('notification_received');
    res.json({ "id": `${id}`, "tc": timeCalled, "req":{"tc":tCall}})
    console.info(`id: ${id}`)
  } catch (ex) {
    res.json({ "ex": `${ex}` })
    console.error(`${ex}`)
  }
}

/*
関数のデプロイ: https://firebase.google.com/docs/functions/manage-functions?hl=ja
*/