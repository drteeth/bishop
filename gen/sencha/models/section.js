Ext.regModel('Section', {
    fields: [
	{name: 'AdContentKey', type: 'string'},
	{name: 'ID', type: 'int'},
	{name: 'MobileTemplate', type: 'string'},
	{name: 'Name', type: 'string'},
	{name: 'Type', type: 'string'},
	
	],
		
	associations: [	        
	{type: 'hasMany', model: 'Gallery', name: 'Galleries'},
	{type: 'hasMany', model: 'Story', name: 'Stories'},
	
	]
});
