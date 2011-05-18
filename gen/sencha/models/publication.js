Ext.regModel('Publication', {
    fields: [
	
	],
		
	associations: [	        
	{type: 'hasMany', model: 'Edition', name: 'Editions'},
	{type: 'hasMany', model: 'Section', name: 'Sections'},
	
	]
});
