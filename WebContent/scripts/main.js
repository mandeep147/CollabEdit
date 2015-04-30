var from,charAdd, inter=-1,prevAddLine=-1,prevAddCh=-1,newData,displayLine=-1,otherUserLine=-1,otherUserName='', currLoc;
var webSocket;
var messages = [];
var index=-1;
var firstMessage;
var to=[];
var path = document.URL;
var ans;
var response;
var creator, currLine=-1, currCh=-1,sent=0; //0 means No

savedBool();
//updateUserCursorPosition();
/*
 * When save button is pressed, page refreshes, then in order to display the
 * prompt, savedBool is used
 */ 
function updateUserCursorPosition(Line)
{
	var data="sg";
	$('#userData').html(otherUserName+", is working at line: "+Line);
}
function savedBool()
{
	var path = document.URL;
	var check = path.split("?");
	if(check!=path)
	{
		$('.noticeText').html("");
		$('.noticeText').append("<div class='errorData'>Data saved</div>");
		$('.noticeText').append("<br><input type='button' value='Back' class='goBack'>");
		$('.noticeForOldData').slideToggle(500);
		$('.goBack').click(function(event)
		{
			$('.noticeForOldData').slideToggle(500);
		});

	}
}


//Getting the loggedInUserNAme and FileName from the server
$.ajax({
    method : "POST",
    async : false,
    url : "/CollabEdit/GetInfo",
    success : function(data) {
       // console.log("data sent successfully: ",data);
    },
    error : function(data) {
       // console.log("Unsuccessful transmission " + data);
    }}).done(
        function(data) {
        	from = data['email'];
        	file = data['fileName'];
            firstMessage = {
                "from":data['email'],
                "file" : data['fileName']
            };
    });


//for getting the data from the DB inorder to add to Codemirror
$.ajax({
			url: '/CollabEdit/CollabData',
			type: 'POST',
			async : false
		})
		.done(function(data,status) {
			if(data['data']!=undefined)
			{
				cm.setValue(data['data']);
			}
		});


//this will get the users with whom this file is being shared
$.ajax({
    method : "POST",
    async : false,
    url : "/CollabEdit/DisplaySEmails"
    }).done(
        function(data) {
        	to = data;
    });

//Saving the changes to the Database
/*
 * Getting the data form codemirror 
 * sending it to OTServer.java
 */
function saveChanges()
{
	
	var json = { 
				'CodeFromEditor': cm.getValue(),
				'file' : file,
				'from' : from
				};

	for(var i  in to)
	{
		json[i.valueOf()] = to[i];
	}
	this.send(json);
	}

$('#logoutButton').click(function()
{
	$.ajax({
		url: '/CollabEdit/Logout',
		method : "POST",
		data: 
		{
			"logout": "logout"
		}
	}).done(function(data,status) {
		window.location.assign("index.html");
	});
});

//mailButton click function
$('#mailButton').click(function()
{
	$('.noticeText').html("");
	$('.noticeText').append("<input type='email' id='emailTo' placeholder='Enter E-mail ID ' required>");
	$('.noticeText').append("<div class='noticeButtonsDiv'><input type='button' id='sendMail' onclick='sendMail()'value='Send'> <input type='button' id='backButton' onclick='getBack() 'value='Back'></div>");
	$('.noticeForOldData').slideToggle(1000);
	
	$('#backButton').click(function(event)
	{
		$('.noticeForOldData').slideToggle(1000);
	});
	
	//Send Email
	$('#sendMail').click(function(event)
	{
		$('.noticeForOldData').slideToggle(1000);
		var userId = document.getElementById('emailTo').value;
		var regEx = /@gmail\.com/;
		if(userId.match(regEx))
		{
			$.ajax({
				data: {
					data: userId
				},
				type: "POST",
				url: "/CollabEdit/MailServlet"			
			}).done(function(data,status)
			{
				if(data['result']=='success')
				{
					alert('Mail Sent');
				}
				else
				{
					$('.noticeText').html("");
					$('.noticeText').append("<div class='errorData'>Mail Did not Sent, Check the E-mail ID and try again</div>");
					$('.noticeText').append("<br><input type='button' value='Back' class='goBack'>");
					$('.noticeForOldData').slideToggle(500);
					$('.goBack').click(function(event)
					{
						$('.noticeForOldData').slideToggle(500);
					});			
				}
			});

		}
		else
		{
			$('.noticeText').html("");
			$('.noticeText').append("<div class='errorData'>Enter Email Id, then click send</div>");
			$('.noticeText').append("<br><input type='button' value='Back' class='goBack'>");
			$('.noticeForOldData').slideToggle(500);
			$('.goBack').click(function(event)
			{
				$('.noticeForOldData').slideToggle(500);
			});			

		}
		});
	});
	


//OnChange Method of CodeMirror
require([ 'dojo/on' ], function(on) {
	
	
	cm.on('beforeChange',function(cm,change) {
		console.log("in before change ");
		console.log("currLoc: "+currLoc+"  otherUserLine: "+otherUserLine);
	    if (currLoc==otherUserLine&&creator!=from) {
	    	console.log("CanCELLLLLLLLLLLLLLLL");
	    	console.log("currLoc: "+currLoc+" line: "+change.from.line);
	    	console.log("totall: "+change);
	    	console.log("Stringify: totall: "+JSON.stringify(change));
	        change.cancel();
	    }
	});

	
	
	cm.on('change', function(arg1, arg2) {
		sent=0;
	//	alert('stringify: all: '+JSON.stringify(arg2));
		actionToPerform = arg2.origin;
		lineLocation = arg2.from.line;
		charLocation = arg2.from.ch;
		data1 = arg2.text;
//		alert('actionToPerform: '+actionToPerform);
/*		if(actionToPerform=='+delete'||actionToPerform=='cut')
		{
	//		alert('inside hu');
			console.log("----------------adding dataRemoved............");
			dataRemoved = arg2.removed[0];
		}
*/		currLine = lineLocation;
		currCh = charLocation;
		charAdd = data1[0];
		var data = {
				"file" : file,
				"from" : from,
				"action" :  actionToPerform  ,
				"data" : data1  ,
				"lineLocation" : lineLocation ,
				"charLocation" :  charLocation 
			}
		
		console.log("poora: "+arg2);
		for(var i  in to)
		{
			data[i.valueOf()] = to[i];
		}
		if(response!=undefined)
		{
			data[response] = response.valueOf();
				response = undefined;
		}
		else
		{
			currLoc = lineLocation+1;
		}
		if(data['action']=='cut')
		{
			data['lastL'] = arg2.to.line;
			data['lastC'] = arg2.to.ch;
		}
//		console.log("query: "+data);
		
//		console.log("currLine: "+currLine+" currCh: "+currCh+" creator: "+creator+"  from: "+from+"  sent: "+sent);
		//alert('check');
	
	//	alert("in dojo: otherUserLine: "+otherUserLine+" currLoca: "+lineLocation+"  data-response: "+data['response']+' creator: '+creator+" from: "+from);
/*		if(otherUserLine==lineLocation&&creator!=from&&data['response']!='response')
		{
			console.log("Re-Doing-----");
			if(actionToPerform=='+input'||actionToPerform=='paste')
			{
				console.log("input/paste");
				deleteAtCursor(cm,data1[0],lineLocation,charLocation );
			}
			else if(actionToPerform=='+delete')
			{
				console.log("delete");
				insertAtCursor(cm,dataRemoved,lineLocation,charLocation);
			}
			else if(actionToPerform=='cut')
			{
				console.log("cut");
				insertAtCursor(cm,dataRemoved,lineLocation,charLocation);
			}
			//alert('dont write bitchwa :D ');
		}
		else
		{
*/			//alert("sedning resposen");
			this.send(data);
//		}
	});
	
});




//calling the openSocket
openSocket();

//FirstMessage: from, to


$.ajax({
	url: '/CollabEdit/GetCreator',
	method : "POST",
	data: firstMessage,
	success: function(data)
	{
		//alert("data to GetCreator");
	},
	error: function(data)
	{
//		alert("Errrors to GetCreator");
	}
}).done(function(data,status) {
//	alert("Got the response: "+data);
//	alert("stringify: "+(JSON.stringify(data)));
	creator = data['creator'];
	firstMessage['creator'] = data['creator'];
});

for(var i  in to)
{
	if(i.valueOf()!='total')
		firstMessage[i.valueOf()] = to[i];
}

/*alert("to: "+to);
alert("strng: "+(JSON.stringify(to)));*/
this.send(firstMessage);
//WebSocket functions

function openSocket(){
    // Ensures only one connection is open at a time
    if(webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED){
        //writeResponse("WebSocket is already opened.");
        return;
    }
    // Create a new instance of the websocket
    webSocket = new WebSocket("ws://192.168.1.103:8080/CollabEdit/main");
    
    /**
     * Binds functions to the listeners for the websocket.
     */
     //calls onOpen of server when connection is established
    webSocket.onopen = function(event){
        if(event.data === undefined)
            return;
        writeResponse(event.data);
    };

    
    //Sending the message to the Server
    
    this.send = function (message, callback) {
        this.waitForConnection(function () {
        	
        	var json = JSON.stringify(message);
        	console.log("this send: "+json);
            webSocket.send(json);
            sent=1;
            if (typeof callback !== 'undefined') {
              callback();
            }
        }, 1000);
    };

    /*
     * 	If a connection is not created instantly, then it will 
     *	keep trying until connection is created
     */
    this.waitForConnection = function (callback, interval) {
        if (webSocket.readyState === 1) {
            callback();
        } else {
            var that = this;
            // optional: implement backoff for interval here
            setTimeout(function () {
                that.waitForConnection(callback, interval);
            }, interval);
        }
    };

  //Calls onMessage ofServer when a message is sent
    webSocket.onmessage = function(event){
        writeResponse(event.data);
    };

   ///calls onClose when connection is closed
    webSocket.onclose = function(event){
        messages.innerHTML += "<br/>" + "Connection closed";
    };
}

/**
 * Sends the value of the text input to the server
 */

function closeSocket(){
    webSocket.close();
}
//displayLine value... 



function writeResponse(json){
	inter=0;
	

	var response = JSON.parse(json);
    var resp = response['response'];
    var line = response['lineLocation'];
    var char = response['charLocation'];
    var data =  response['data'];
    var save = response['CodeFromEditor'];

   
 //   alert('writing response, check resp: '+resp);
    
    //Other person's location line
    if(resp==undefined)
    {
    	alert('otherUserLine: '+(line+1));
    	otherUserLine = line;
    	otherUserName = response['from'];
    	//alert('line: '+otherUserLine);
    }
    
    updateUserCursorPosition(line+1);
  //  alert('new Location: '+otherUserLine);
    //Display that user2 is working on this line
//	alert('location of second person: '+otherUserLine);    
    if(save!=undefined)
    	newData = data[0];
    prevAddLine = line;
    prevAddCh = char;
   // alert('writing data: '+data[0]+ " line: "+line+" char: "+char);
    if(data!=undefined&&line!=undefined&&char!=undefined)
    {
    	if(data[0]=='}')
    	{
    		closingBracs(cm, data[0], line, char);
    	}
    	else if(response['action']=='+delete')
    			deleteAtCursor(cm,data[0],line,char);
    	else if (response['action']=='+input')
    	{
    		if(data.length>1)
    		{
    			insertEnterCursor(cm, "\n", line, char);
    		}
    		else if(data[0].length>0)
    		{
    		//	alert('inserting now: line: '+line+" char: "+char+" data: "+data[0]);
    			insertAtCursor(cm,data[0],line,char);
    		}
    	}
    	else if (response['action']=='cut')
    	{
    		cutAtCursor(cm,"",line,char,response['lastL'],response['lastC']);
    	}
    	else if (response['action']=='paste')
    	{
    		insertAtCursor(cm,data[0],line,char);
    	}
    	else if (save!=undefined)
    	{
    		alert("data saved");
    	}
    }
   }

//Interchange
function interchange(instance, text,line,char)
{
	response = 'response';
	var prev = instance.getCursor();
	var data = cm.getLine(line);
	if(data.length>0)
	{
		var one = data.charAt(char);
		var two = data.charAt(char+1);
		//alert('one: '+one+" two: "+two);
		var newLine = data.substring(0,char)+two+one+data.substring(char+2,data.length);
		var finalData = cm.getValue();
		
		var arr = finalData.split(data);
		
		var finalData = arr[0]+newLine+arr[1];
			
		cm.setValue(finalData);
		
		instance.setCursor(prev.line,  prev.ch);
		instance.focus();	

	}

}




//Cut wala scene
function cutAtCursor(instance, text,line,char,tillLine, tillChar) {
	response = 'response';
	var prev = instance.getCursor(); //prev 1,5
	var startLine = line;
	while(tillLine-line>=0)
	{
	
		var data = cm.getLine(line);
		if(data.length>0)
		{
			if(tillLine-line==0)
			{
				var newLine = data.substring(0,char)+data.substring(tillChar,data.length);
			}
			else
			{
				var newLine = data.substring(0,char);
			}
			var finalData = cm.getValue();
			
			var arr = finalData.split(data);
			
			var finalData = arr[0]+newLine+arr[1];
				
			cm.setValue(finalData);
			char=0;
			line++;
		}
	}
	var end = cm.lastLine();
	var totalShift = tillLine-startLine;
	var d1 = cm.getLine(startLine);
	var d2 = cm.getLine(tillLine);
	if(d1.length>0&&d2.length>0)
	{
		for(var index = tillLine;index<end+1;index++)
		{
			
		}
	}
/*	instance.setCursor(prev.line,  prev.ch);
	instance.focus();	
*/	
	
	instance.setCursor(prev.line,  prev.ch); //1,5
//	alert('cut ho gya h ');
	instance.focus();	
}

function deleteLine(instance,line)
{
	
	var newLine = data.substring(0,char)+data.substring(char+1,data.length);
	var finalData = cm.getValue();
	
	var arr = finalData.split(data);
	
	var finalData = arr[0]+newLine+arr[1];
		
	cm.setValue(finalData);
	
	instance.setCursor(prev.line,  prev.ch);
	instance.focus();	

}
//Closing Bracs results in Indentation
function closingBracs(instance, text,line,char)
{
	response = 'response';
	var prev = instance.getCursor();
	instance.setCursor({line: line , ch : char });
	instance.replaceSelection(text);

	cm.operation(function(){
		cm.indentLine(line,'smart');
	});
	
	instance.setCursor(prev.line,  prev.ch);
	instance.focus();
}

function insertAtCursor(instance, text,line,char) {
	response = 'response';
	var prev = instance.getCursor(); //prev 1,5
	instance.setCursor({line: line , ch : char }); //0,3
	instance.replaceSelection(text); //added text
	instance.setCursor(prev.line,  prev.ch); //1,5
	instance.focus();	
	}



//Delete method
function deleteAtCursor(instance, text,line,char) {
	response = 'response';
	var prev = instance.getCursor();
	var data = cm.getLine(line);
	if(data.length>0)
	{
		var newLine = data.substring(0,char)+data.substring(char+1,data.length);
		var finalData = cm.getValue();
		
		var arr = finalData.split(data);
		
		var finalData = arr[0]+newLine+arr[1];
			
		cm.setValue(finalData);
		
		instance.setCursor(prev.line,  prev.ch);
		instance.focus();	

	}
}

//insert method
function insertAtCursor(instance, text,line,char) {
	response = 'response';
	var prev = instance.getCursor(); //prev 1,5
	instance.setCursor({line: line , ch : char }); //0,3
	instance.replaceSelection(text); //added text
	instance.setCursor(prev.line,  prev.ch); //1,5
	instance.focus();	
	}

//whenever Enter is pressed, "" is sent as a change by ccodemirro
function insertEnterCursor(instance, text,line,char) 
{
	alert("enter called");
	response = 'response';
	var prev = instance.getCursor();
	instance.setCursor({line: line , ch : char });
	instance.replaceSelection(text);
	instance.setCursor(prev.line,  prev.ch);
	instance.focus();
}
