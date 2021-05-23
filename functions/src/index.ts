/* eslint-disable */
import * as firebase from "firebase-admin"
import * as functions from "firebase-functions"
import axios, { AxiosResponse } from 'axios'
import { RuntimeOptions } from "firebase-functions"

const runtimeOpts = {
  timeoutSeconds: 60,
  memory: '512MB'
} as RuntimeOptions
const region = "asia-northeast2"

const axiosClient = axios.create({
  baseURL: `https://${region}-stress1.cloudfunctions.net`,
  headers: { 'Content-Type': 'application/json' },
  timeout: 300 * 1000, // milliseconds
})

firebase.initializeApp();
const firestore = firebase.firestore();

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
        await sleep(1)
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
    try {
      const timeCalled = Date.now()
      const launchId = req.query.id as string
      const nr = parseInt(req.query.nr as string)
      const nm = parseInt(req.query.nm as string)
      const timeToStart = parseInt(req.query.ts as string)
      var proms = Array<Promise<AxiosResponse>>()
      for (var i = 0; i < nr; ++i) {
        await sleep(1)
        const pr = axiosClient.get(`/startAt/?id=${launchId},${i}&n=${nm}&ts=${timeToStart}`)
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
      //res.json({ "res": `${rs}` })
    } catch (ex) {
      res.json({ "ex": `${ex}` })
    }
  })

// 指定時刻tまで待ち同時にn回Write
// .../startAt/?id=<dev_id>&n=<writes>&ts=<start_time>
export const startAt = functions
  .runWith(runtimeOpts)
  .region(region)
  .https.onRequest(async (req, res) => {
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

/*
関数のデプロイ: https://firebase.google.com/docs/functions/manage-functions?hl=ja
*/