/*
 * This will send the ajax request to the server
 * in order to retrieve the FileNames which are
 * either created by the LoggedIn user or those which
 * have been shared with. 
 */
$.ajax({
	method : "POST",
	url : "/CollabEdit/DisplaySEmails",
	error : function(data) {
		console.log("error " + data);
	}
}).done(
		function(data) {
			var completeData = data;
			var length = data['total'];
			for (var i = 0; i < length; i++)
				$('#usersOnline').append(
						"<div class='perUser'>" +
						"<img src='/CollabEdit/images/GreenDot.png' class='greenDot'></img>" +
						"<span class='online'>"
								+ completeData['email' + (i + 1)] + "</span></div>");
		});
