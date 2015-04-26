var webSocket;

var indentVar;
var checkIndent=0;
var messages = [];
var index=-1;
//var link = "ws" + document.URL.substring(4, document.URL.length) ;
var link = "http://localhost:8080/CollabEdit/Document/kk424796576E-FEDD-E411-829A-F82FA8BE8622";
var firstMessage;
var to=[];
var path = document.URL;
var array = path.split("/");
var temp = array[array.length - 1];
var link2 = temp.substring(temp.length - 36, temp.length);
var ans;
var response;

savedBool();

function savedBool()
{
	var path = document.URL;
	var check = path.split("?");
	if(check!=path)
	{
		alert('Data saved');
	}
}



$.ajax({
    method : "POST",
    async : false,
    url : "/CollabEdit/GetInfo",
    success : function(data) {
        console.log("data sent successfully: ",data);
    },
    error : function(data) {
        console.log("Unsuccessful transmission " + data);
    }}).done(
        function(data) {
        	from = data['email'];
        	file = data['fileName'];
            firstMessage = {
                "from":data['email'],
                "file" : data['fileName']
            };
    });


//for getting the data to add to CCdoemirror
$.ajax({
			url: '/CollabEdit/CollabData',
			type: 'POST',
			async : false,
			success: function(data){
				//alert("askking from db successfull");
			}
		})
		.done(function(data,status) {
//			alert("adding to teaxtarea data: "+data);
			if(data['data']!=undefined)
			{
				indentVar = 'prev';
	//			alert("in the IF condition: adding this: "+data['data']);
				cm.setValue(data['data']);
			}
		/*	else
				alert("FAILS IF condition: adding this: "+data['data']+ "full json: "+data);*/
	});


//this will get the users with whom this file is being shared
$.ajax({
    method : "POST",
    async : false,
    url : "/CollabEdit/DisplaySEmails",
    data: {"url": link2},
    success : function(data) {
    	//alert("data to DSE sent success");
        console.log("data sent successfully: ",data);
    },
    error : function(data) {
    	//alert("NOOOOOOOOOOOOOOO data to DSE sent success");
        console.log("Unsuccessful transmission in MAIN.JS-----------------" + data);
    }}).done(
        function(data) {
        	//alert("data to DSE sent success RESPOSE: "+data);
        	//alert("asli data: "+(JSON.stringify(data)));
        	//alert("in data function");
        	console.log("-------------------------------------");
        	console.log("in data function");
        	to = data;
//            ans = to; //JSON.stringify(to);
    });
function saveChanges()
{
	
	var json = { 
					'CodeFromEditor': cm.getValue(),
					'file' : file,
					'from' : from
				};
	for(var i  in to)
	{
//		alert("value of i: "+i);
		json[i.valueOf()] = to[i];
	}

	console.log("-----saving-- this: ", json);
	//alert("data: "+json);
	this.send(json);
/* 		$.ajax({
		url:s '/TryKrRahaHu/SaveData',
		method : "POST",
		data: json
	}).done(function(data,status) {
			var res = data['result'];
			if(res=='success')
			{
				alert('done');	
			}
			else
			{
				alert('panga');	
			}
		})
*/	}

$('#logoutButton').click(function()
{
	/*alert("logout wala");*/
	$.ajax({
		url: '/CollabEdit/Logout',
		method : "POST",
		data: 
		{
			"logout": "logout"
		}
	}).done(function(data,status) {
		console.log('back from server');
		window.location.assign("index.html");
		});
});

$('#mailButton').click(function()
{
	$('.noticeText').html("");
	$('.noticeText').append("<input type='email' id='emailTo' placeholder='Enter E-mail ID ' required>");
	$('.noticeText').append("<div class='noticeButtonsDiv'><input type='button' id='sendMail' onclick='sendMail()'value='Send'> <input type='button' id='backButton' onclick='getBack() 'value='Back'></div>");
	$('.noticeForOldData').slideToggle(1000);
	
	//alert("here111");
	//Back Click Event
	$('#backButton').click(function(event)
	{
		$('.noticeForOldData').slideToggle(1000);
	});
	
	//Send Email
	$('#sendMail').click(function(event)
	{
		var userId = document.getElementById('emailTo').value;
		$.ajax({
			data: {
				data: userId
			},
			type: "POST",
			url: "/CollabEdit/Mail",
			success: function(data){
				//alert("data sent");
			},
			error: function(data){
				//alert("error aata");
			} 
			
		}).done(function(data,status)
		{
			$('.noticeText').html("");
			$('.noticeText').append("<div class='errorData'>Mail Sent</div>");
			$('.noticeText').append("<br><input type='button' value='Back' class='goBack'>");
			$('.noticeForOldData').slideToggle(500);
			$('.goBack').click(function(event)
			{
				$('.noticeForOldData').slideToggle(500);
			});
		});
	});
	
});


require([ 'dojo/on' ], function(on) {
	cm.on('change', function(arg1, arg2) {
		actionToPerform = arg2.origin;
		console.log("typde dekh: ", typeof actionToPerform);
		lineLocation = arg2.from.line;
		charLocation = arg2.from.ch;
		data1 = arg2.text;
		console.log("Action: ", actionToPerform, " Data: ", data, " line: ",
				lineLocation, " charLocation: ", charLocation);
		console.log("Location: " + window.location.href);
		
		var data = {
				"file" : file,
				"from" : from,
				"action" :  actionToPerform  ,
				"data" : data1  ,
				"lineLocation" : lineLocation ,
				"charLocation" :  charLocation 
			}
		//alert("to: "+to);
		//alert("check to: "+JSON.stringify(to));
		for(var i  in to)
		{
			//alert("value of i: "+i);
			data[i.valueOf()] = to[i];
		}
		if(response!=undefined)
		{
		//	alert("in the response");
			data[response] = response.valueOf();
				response = undefined;
		}
		console.log("-------------------------");
		console.log("sending this: "+JSON.stringify(data));
		this.send(data);
		
	});
});

var from ;


//calling the openSocket
openSocket(link);

console.log("before sending first messgae: "+firstMessage);
//alert("before sending first messgae: "+firstMessage['from']);
this.send(firstMessage);
//alert("first msg sent");



function openSocket(link){
    // Ensures only one connection is open at a time
    if(webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED){
        //writeResponse("WebSocket is already opened.");
        return;
    }
    // Create a new instance of the websocket
    console.log("LINK ISSSSSSSSSSS" + link);

    webSocket = new WebSocket("ws://192.168.1.103:8080/CollabEdit/main");
    /**
     * Binds functions to the listeners for the websocket.
     */
     //calls onOpen of server when connection is established
    webSocket.onopen = function(event){
        if(event.data === undefined)
            return;
		//alert("onOpen executed");
        writeResponse(event.data);
    };

    
    
    this.send = function (message, callback) {
        this.waitForConnection(function () {
        	
        	var json = JSON.stringify(message);
        	//alert("trying to send this: "+json);
            webSocket.send(json);
            if (typeof callback !== 'undefined') {
              callback();
            }
        }, 1000);
    };

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

//Data is to SEND hona h 
function sendText(data){
	
	console.log("-------------in the SENDTEXT--------------");
	console.log("typeof data: ",typeof data);
    var json = JSON.stringify(data);
 //   alert("sending noww data: "+json);
    console.log("typeof json: ",typeof json," and data is: ",json);
    console.log("-------------in the SENDTEXT--------------");
    webSocket.send(json);
}

function closeSocket(){
    webSocket.close();
}

///jado response aaega MEANS jab data aaega
function writeResponse(json){
    var response = JSON.parse(json);
  //  alert("Receieve data from other USER: "+response);
    console.log("??????????????????????stringify; "+JSON.stringify(response));
    console.log("response dkeh zra: "+response);
    
    var line = response['lineLocation'];
    console.log("typeof line: "+typeof line);
    
    var char = response['charLocation'];
    console.log("typeof char: "+typeof char);
     
    var data =  response['data'];
    console.log("typeof data: "+typeof data);
    
    var save = response['CodeFromEditor'];
    if(save!=undefined)
    {
    	//alert("data saved");
    }
    else if(data!=undefined&&line!=undefined&&char!=undefined)
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
    			insertAtCursor(cm,data[0],line,char);
    	}
    	//indentAll(cm);
    }
   }

function closingBracs(instance, text,line,char)
{
	response = 'response';
	console.log("ressssssssspooooooonnnnnnnnnnnssssssssssseeeeee: "+response);
	var prev = instance.getCursor();
	instance.setCursor({line: line , ch : char });
	instance.replaceSelection(text);

	cm.operation(function(){
		cm.indentLine(line,'smart');
	});
	
	instance.setCursor(prev.line,  prev.ch);
	instance.focus();
	
	alert('end of }');
}

function deleteAtCursor(instance, text,line,char) {
	response = 'response';
	console.log("ressssssssspooooooonnnnnnnnnnnssssssssssseeeeee: "+response);
	//alert("in delete");

	var data = cm.getLine(line);
	var newData = data.substring(0,char)+data.substring(char+1,data.length);
	
	var saaraData = cm.getValue();
	
	var arr = saaraData.split(data);
	
	var finalData = arr[0]+newData+arr[1];
		
	cm.setValue(finalData);
	
	instance.setCursor(prev.line,  prev.ch);
	instance.focus();	
	}
function insertAtCursor(instance, text,line,char) {
	response = 'response';
	console.log("ressssssssspooooooonnnnnnnnnnnssssssssssseeeeee: "+response);
	var prev = instance.getCursor();
	instance.setCursor({line: line , ch : char });
	instance.replaceSelection(text);
	/*if(enteraaya==2)
	{
		
	}
	if(enteraaya>=1)
	{
		enteraaya++;
	}	*/
	
	instance.setCursor(prev.line,  prev.ch);
	instance.focus();
	
	}
function insertEnterCursor(instance, text,line,char) 
{
	response = 'response';
	console.log("ressssssssspooooooonnnnnnnnnnnssssssssssseeeeee: "+response);
	var prev = instance.getCursor();
	instance.setCursor({line: line , ch : char });
	instance.replaceSelection(text);
	var temp = instance.getCursor();
	instance.setCursor(prev.line,  prev.ch);
	instance.focus();
	enteraaya = 1;
	}
