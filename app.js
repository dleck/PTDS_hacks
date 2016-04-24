var events = require('events');
var eventEmitter = new events.EventEmitter();

var endHandler = function restart() {
	console.log("Inside handler!!!!");
	run();
}

eventEmitter.on('ended', endHandler);




/*------------------------------------------------------------------------------
PUBNUB, BABY
-------------------------------------------------------------------------------*/
var pubnub = require("pubnub")({
    ssl           : true,  // <- enable TLS Tunneling over TCP
    publish_key   : "pub-c-c4775583-2521-4e1f-b853-cdb74ae25cb3",
    subscribe_key : "sub-c-9dbe6968-0995-11e6-8c3e-0619f8945a4f"
});
  
/* ---------------------------------------------------------------------------
Publish Messages
--------------------------------------------------------------------------- */
var message = { "Hello" : "World!" };
pubnub.publish({ 
    channel   : 'PTDS_channel',
    message   : message,
    callback  : function(e) { console.log( "SUCCESS!", e ); },
    error     : function(e) { console.log( "FAILED! RETRY PUBLISH!", e ); }
});
  
/* ---------------------------------------------------------------------------
Listen for Messages
--------------------------------------------------------------------------- */
pubnub.subscribe({
    channel  : "PTDS_channel",
    callback : function(message) {
        console.log( " > ", message );
	interpretMessage(message);
    }
});


/*---------------------------------------------------------------------------------
EDISON, BABY
-------------------------------------------------------------------------------------*/ 
var mraa = require('mraa');

// PIN SETUP**********************************************
var redLed = new mraa.Gpio(2);
redLed.dir(mraa.DIR_OUT);
redLed.write(1);

var greenLed = new mraa.Gpio(4);
greenLed.dir(mraa.DIR_OUT);
greenLed.write(1);

var buzzer = new mraa.Gpio(8);
buzzer.dir(mraa.DIR_OUT);
buzzer.write(0);

var forceRes = new mraa.Aio(0);


// LOGIC/CONTROL VARS*********************************************
var armed = false;
var stolen = false;
var nobreak = true;

var newWeight = forceRes.read();
var lastWeight = forceRes.read();
var lastTime = new Date();
var newTime = new Date();
var checkWeight = false;
var weightViolation = false;


//arm();
//armed = true;
disarm();

function loop() {
	while(nobreak) {
		run();
	}
}

//while (1) {
	//run();
//}

function run() {
	if (armed && !stolen) {
		monitorPackage();
	}

	else if (!armed && !stolen) {
		//waitForArm();
	}
	else if (armed && stolen) {
	}

};


// CONTROL FUNCTIONS******************************************

function waitForArm() {
	console.log("waiting for arm...");
	// This function could be empty for the same desired effect, but this ensures
	// nothing happens until edwin is armed
	while(!armed) {}
	arm();
};

function waitForDisarm() {
	console.log("waiting for disarm...");
	while(armed) {}
	disarm();
};

function monitorPackage() {
	checkTheft();
	if (stolen) {
		triggerAlarm();
	}
};

function checkTheft() {
	if ((newTime - lastTime) > 750 || weightViolation) {
		newWeight = forceRes.read();
		console.log("checking for theft " + newWeight);
		if (!weightViolation && (lastWeight - newWeight) > ((1024 - lastWeight)*.25)) {
			console.log("potential violation!!!!!!!!!!!!!!!!!!!!");
			weightViolation = true;
			lastTime = new Date();
		}
		else if (weightViolation) {
			newTime = new Date();
			if ((newTime - lastTime) > 1250) { // in milliseconds
				// check if there's still a weight violation
				if ((lastWeight - newWeight) > ((1024 - lastWeight)*.1)) {
					stolen = true;
					//triggerAlarm();
				}
				else {
					weightViolation = false;
				}
			}
		}
		else {
			//if ((newWeight - lastWeight) > ((1024 - lastWeight)*.25)) {
			//	publishDelivered();
			//}

			lastTime = new Date();
			lastWeight = newWeight;
		}

	}

	
	if (!weightViolation) {
		newTime = new Date();
	}
	else {
	}
};

function triggerAlarm() {
	console.log("Triggering alarm!");
	buzzerOn();
	//waitForDisarm();
	//buzzerOff();
	nobreak = false;
	publishStolen();
};

function arm() {
	console.log("arming...");
	armed = true;
	redOn();
	greenOff();
	stolen = false;
	loop();
	publishArmed();
};

function disarm() {
	console.log("disarming...");
	armed = false;
	stolen = false;
	greenOn();
	redOff();
	buzzerOff();
	publishDisarmed();
};



//PUBNUB CONTROL FUNCTIONS**********************************
function interpretMessage(mess) {
	console.log("Received PubNub Message: " + mess);
	switch (mess) {
		case 'app:edwinArm' : arm();
			break;
		case 'app:edwinDisarm' : disarm();
			break;
		default :
			//something is wrong here
			console.log("Nothing initated");
			break;

	}
};




// HELPER FUNCTIONS****************************************
function redOn() {
	redLed.write(1);
};
function greenOn() {
	greenLed.write(1);
};
function buzzerOn() {
	buzzer.write(1);
};
function redOff() {
	redLed.write(0);
};
function greenOff() {
	greenLed.write(0);
};
function buzzerOff() {
	buzzer.write(0);
};


function publishArmed() {
	var armedyo = "edwin:armed";
	pubnub.publish({ 
	    channel   : 'PTDS_channel',
	    message   : armedyo,
	    callback  : function(e) { console.log( "SUCCESS!", e ); },
	    error     : function(e) { console.log( "FAILED! RETRY PUBLISH!", e ); }
	});
};
function publishDisarmed() {
	var armedyo = "edwin:disarmed";
	pubnub.publish({ 
	    channel   : 'PTDS_channel',
	    message   : armedyo,
	    callback  : function(e) { console.log( "SUCCESS!", e ); },
	    error     : function(e) { console.log( "FAILED! RETRY PUBLISH!", e ); }
	});
};

function publishStolen() {
	var armedyo = "edwin:stolen";
	pubnub.publish({ 
	    channel   : 'PTDS_channel',
	    message   : armedyo,
	    callback  : function(e) { console.log( "SUCCESS!", e ); },
	    error     : function(e) { console.log( "FAILED! RETRY PUBLISH!", e ); }
	});
};

function publishDelivered() {
	var armedyo = "edwin:delivered";
	pubnub.publish({ 
	    channel   : 'PTDS_channel',
	    message   : armedyo,
	    callback  : function(e) { console.log( "SUCCESS!", e ); },
	    error     : function(e) { console.log( "FAILED! RETRY PUBLISH!", e ); }
	});
};



//***************************RESTART CALLBACK HOMIE***************************
//eventEmitter.emit('ended');



