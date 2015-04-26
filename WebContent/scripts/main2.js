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
			else if(data['file']!=undefined)
			{
				switch(data['file']){
					case '.js':
					{
						mode = 'javascript';
						break;
					}
					case ".css":{
						mode = 'css';
						break;
					}
					case ".rb":{
						mode = 'ruby';
						break;
					}
					case ".vb":{
						mode = 'vb';
						break;
					}
					case ".pl":{
						mode = 'perl';
						break;
					}
					case ".php":{
						mode = 'php';
						break;
					}
					case ".asp":
					case '.c':
					case '.cpp': 
					case '.java': {
						mode = 'clike';
						break;
					}
					case ".jsp":
					case ".html":{
						mode =  'xml';
						break;
					}
				}
				if(mode!=undefined)
					modeLoc = 'mode'+"/"+mode+"/"+mode+".js"; 
			}
		/*	else
				alert("FAILS IF condition: adding this: "+data['data']+ "full json: "+data);*/
	});
