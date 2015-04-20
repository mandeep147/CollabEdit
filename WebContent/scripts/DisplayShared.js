var path = document.URL;
var array = path.split("/");
var temp = array[array.length - 1];
var ans = temp.substring(temp.length - 36, temp.length);

console.log("path dekhi: " + path);
console.log("sending this filename: " + ans);

$.ajax({
	method : "POST",
	url : "/CollabEdit/DisplaySEmails",
	data : {
		url : ans
	},
	success : function(data) {
		// yup run kr isse :P
		alert("data sent successfully for Displayy");
		console.log("dekh raha hu bs")
		console.log("in success, got this: ", data);
		console.log("type dekh succes ch: ", typeof data);
		console.log(data['email1']);
	},
	error : function(data) {
		console.log("error " + data);
	}
}).done(
		function(data) {
			alert("Response for shared Emails: " + data);
			
			var completeData = data;
			var length = data['total'];
			for (i = 0; i < length; i++)
				$('#usersOnline').append(
						"<div class='perUser'>" +
						"<img src='/CollabEdit/images/GreenDot.png' class='greenDot'></img>" +
						"<span class='online'>"
								+ completeData['email' + (i + 1)] + "</span></div>");

		});
