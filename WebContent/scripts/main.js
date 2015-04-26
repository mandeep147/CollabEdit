var from ;
var webSocket;
var messages = [];
var index=-1;
var firstMessage;
var to=[];
var path = document.URL;
var ans;
var response;

savedBool();

/*
 * When save button is pressed, page refreshes, then in order to display the
 * prompt, savedBool is used
 */ 

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
		var userId = document.getElementById('emailTo').value;
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
				$('.noticeText').html("");
				$('.noticeText').append("<div class='errorData'>Mail Sent</div>");
				$('.noticeText').append("<br><input type='button' value='Back' class='goBack'>");
				$('.noticeForOldData').slideToggle(500);
				$('.goBack').click(function(event)
				{
					$('.noticeForOldData').slideToggle(500);
				});
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
	});
	
});

//OnChange Method of CodeMirror
require([ 'dojo/on' ], function(on) {
	cm.on('change', function(arg1, arg2) {
		actionToPerform = arg2.origin;
		lineLocation = arg2.from.line;
		charLocation = arg2.from.ch;
		data1 = arg2.text;
		var data = {
				"file" : file,
				"from" : from,
				"action" :  actionToPerform  ,
				"data" : data1  ,
				"lineLocation" : lineLocation ,
				"charLocation" :  charLocation 
			}
		for(var i  in to)
		{
			data[i.valueOf()] = to[i];
		}
		if(response!=undefined)
		{
			data[response] = response.valueOf();
				response = undefined;
		}
		this.send(data);
	});
});




//calling the openSocket
openSocket();

//FirstMessage: from, to
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
            webSocket.send(json);
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


function writeResponse(json){
    var response = JSON.parse(json);
    
    var line = response['lineLocation'];
    var char = response['charLocation'];
    var data =  response['data'];
    var save = response['CodeFromEditor'];
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
    			insertAtCursor(cm,data[0],line,char);
    	}
    }
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
	var prev = instance.getCursor();
	instance.setCursor({line: line , ch : char });
	instance.replaceSelection(text);
	instance.setCursor(prev.line,  prev.ch);
	instance.focus();	
	}

//whenever Enter is pressed, "" is sent as a change by ccodemirro
function insertEnterCursor(instance, text,line,char) 
{
	response = 'response';
	var prev = instance.getCursor();
	instance.setCursor({line: line , ch : char });
	instance.replaceSelection(text);
	instance.setCursor(prev.line,  prev.ch);
	instance.focus();
	}
