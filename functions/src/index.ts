/* eslint-disable */
import * as functions from "firebase-functions";

const admin = require('firebase-admin');
admin.initializeApp();

export const addMessage = functions.https.onRequest(async (req, res) => {
  const width: number = parseInt(req.query.width as string);
  var count = 0;
  var start = Date.now();
  var end = start + 10 * 1000;
  for (var i = 0; Date.now() < end; i = (i + 1) % width) {
    var result = await report(`${i}`)
    if (result != null) count++;
  }
  res.json({ "count": `${count}`, "start": start, "end": end });
});

function report(id: String) {
  return admin.firestore().collection('messages').add(
    {
      "count": id,
      "now": `${Date()}`,
      "now2": `${Date.now()}`,
    }
  );
}
