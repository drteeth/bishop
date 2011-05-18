Ext.regModel('Gallery', {
    fields: [
	{name: 'Title', type: 'string'},
	
	],
		
	associations: [	        
	{type: 'hasMany', model: 'Image', name: 'Photos'},
	
	]
});
