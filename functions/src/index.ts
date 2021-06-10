import { initializeApp } from "firebase/app"
import * as functions from "firebase-functions"
import { getFirestore, collection, addDoc } from "firebase/firestore"

import axios, { AxiosResponse } from 'axios'

const firebaseConfig = {
    apiKey: "AIzaSyA5-SBdtMZ9dTUVkrth1Ss2p1JbH8zjVMs",
    authDomain: "stress1.firebaseapp.com",
    projectId: "stress1",
    storageBucket: "stress1.appspot.com",
    messagingSenderId: "697538713156",
    appId: "1:697538713156:web:1aa57040441fa8a9e00e11",
    measurementId: "G-F1071BTZMH"
};

initializeApp(firebaseConfig)

const runtimeOpts = {
    timeoutSeconds: 540,
    //memory: '512MB'
    memory: '1GB'
    //memory: '2GB'
} as functions.RuntimeOptions
const region = "asia-northeast2"

const axiosClient = axios.create({
    baseURL: `https://${region}-stress1.cloudfunctions.net`,
    headers: { 'Content-Type': 'application/json' },
    timeout: 300 * 1000, // milliseconds
})

export const stress1 = functions.runWith(runtimeOpts).region(region).https.onRequest(async (request, response) => {
    try {
        var proms = Array<Promise<AxiosResponse>>()
        for (var i = 0; i < 1000; ++i) {
            const pr = axiosClient.get(`/target`)
            proms.push(pr)
        }
        var rs = await Promise.all(proms)

        const res = { "res": { "count": rs.length, "time": Date.now() } }
        functions.logger.info("res: ", res)
        response.json({ "res": res });
    } catch (ex) {
        functions.logger.error("Error", ex);
        response.json({ "error": ex });
    }
});


export const target = functions.runWith(runtimeOpts).region(region).https.onRequest(async (request, response) => {
    try {
        const db = getFirestore()
        const docRef = await addDoc(collection(db, "messages"), {
            first: "Ada",
            last: "Lovelace",
            born: 1815
        });
        //        console.log("Document written with ID: ", docRef.id)
        const res = { "res": { "id": docRef.id, "time": Date.now() } }
        functions.logger.info("res: ", res)
        response.json({ "res": res });

    } catch (ex) {
        //        console.error("Error adding document: ", ex);
        functions.logger.error("Error", ex);
        response.json({ "error": ex });
    }

    //functions.logger.info("Hello logs!", { structuredData: true });
    //response.send("Hello from Firebase!");
});
