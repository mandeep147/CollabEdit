
require([ 'dojo/on' ], function(on) {
	cm.on('change', function(arg1, arg2) {
		actionToPerform = arg2.origin;
		console.log("typde dekh: ", typeof actionToPerform);
		lineLocation = arg2.from.line;
		charLocation = arg2.from.ch;
		data = arg2.text;
		console.log("Action: ", actionToPerform, " Data: ", data, " line: ",
				lineLocation, " charLocation: ", charLocation);
		$.ajax({
			method : "POST",
			url : "OTServer",
			data : {
				"action" : "'" + actionToPerform + "'",
				"data" : "'" + data + "'",
				"lineLocation" : "'" + lineLocation + "'",
				"charLocation" : "'" + charLocation + "'"
			},
			success: function(data){
				console.log("in success, got this: ",data);
			},
			error: function(data){
                console.log("siyapa " + data);
           }
		}).done(function(msg) {
			alert("Response: " + msg);
		});
	})
});