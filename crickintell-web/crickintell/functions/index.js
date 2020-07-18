const functions = require('firebase-functions');
const express = require('express');
const engines = require('consolidate');
const admin = require('firebase-admin');
admin.initializeApp({
    credential: admin.credential.cert(require('./cricintell-firebase-adminsdk-qmbcq-dcc4e47873.json'))
});

const db = admin.firestore();
db.settings({ timestampsInSnapshots: true });

const teams = ['Pakistan', 'India', 'England', 'Australia', 'West Indies', 'South Africa', 'New Zealand', 'Sri Lanka', 'Bangladesh', 'Zimbabwe', 'Kenya', 'Afghanistan', 'Hong Kong', 'Netherlands', 'Canada', 'Ireland', 'Nepal', 'Bermuda'];
const app = express();

app.engine('hbs', engines.handlebars);
app.set('views', './views');
app.set('view engine', 'hbs');
app.get('/', (request, response) => {
    response.render('index');
});
app.get('/login', (request, response) => {
    response.render('login');
});
app.post('/login', (request, response) => {

    if (request.body.email == 'cricintellfyp@gmail.com' && request.body.password == 'CricIntell!@#') {
        response.redirect('admin');

    }
    else {
        response.redirect('login?invalid_creds');
    }


});

app.get('/admin', (request, response) => {
    response.render('dashboard');
});
app.get('/ground', (request, response) => {
    response.render('ground');
});

app.get('/odi-match', (request, response) => {
    response.render('odi-match');
});

app.get('/odi-player-inning', (request, response) => {
    response.render('odi-player-inning');
});

app.post('/ground', (request, response) => {
    response.send('PERMISSION_DENIED: Missing or insufficient permissions');
});

app.post('/odi-match', (request, response) => {
    response.send('PERMISSION_DENIED: Missing or insufficient permissions');

});

app.post('/odi-player-inning', (request, response) => {
    response.send('PERMISSION_DENIED: Missing or insufficient permissions');

});
app.get('/logout', (request, response) => {
    response.redirect('/');
})

app.get('/teams/odi', (request, response) => {
    const data = require("./matches-odi.json");
    const collectionKey = "ODIMatches";

    if (data && (typeof data === "object")) {
        Object.keys(data).forEach(docKey => {
            db.collection(collectionKey).doc(docKey).set(data[docKey]).then((res) => {
                console.log("Document " + docKey + " successfully written!");
            }).catch((error) => {
            });
        });
        response.send('Data imported');

    }
});

app.get('/teams/t20', (request, response) => {
    const data = require("./matches-t20.json");
    const collectionKey = "T20Matches";

    if (data && (typeof data === "object")) {
        Object.keys(data).forEach(docKey => {
            db.collection(collectionKey).doc(docKey).set(data[docKey]).then((res) => {
                console.log("Document " + docKey + " successfully written!");
            }).catch((error) => {
                //console.error("Error writing document: ", error);
            });
        });
        response.send('T-20 Data imported');
    }
});
app.get('/test', (request, response) => {
    db.collection('TestMatches').get().then((documents) => {
        documents.forEach((doc, index) => {
            console.log(doc.id);
        });

    }).catch(err => {
        console.log(`error:${err}`);
    });
    response.send('okay report');
});
app.get('/teams/test', (request, response) => {
    const data = require("./matches-test.json");
    const collectionKey = "TestMatches";

    if (data && (typeof data === "object")) {
        Object.keys(data).forEach(docKey => {
            db.collection(collectionKey).doc(docKey).set(data[docKey]).then((res) => {
                console.log("Document " + docKey + " successfully written!");
            }).catch((error) => {
                
            });
        });
        response.send('Data imported');
    }
});
app.get('/players/odi', (request, response) => {
    const data = require("./odi-players-data.json");
    const collectionKey = "ODIPlayers";
    if (data && (typeof data === "object")) {
        Object.keys(data).forEach(docKey => {
            console.log(docKey);
            // db.collection(collectionKey).doc(docKey).set(data[docKey]).then((res) => {
            //     console.log("Document " + docKey + " successfully written!");
            // }).catch((error) => {

            // });
        });
        response.send('');
    }
});
getOpponent = (match, country) => {
    const opponents = match.split(' v ');
    return opponents[0] == country ? opponents[1] : opponents[0];
}

exports.app = functions.https.onRequest(app);
