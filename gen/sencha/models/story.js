Ext.regModel('Story', {
    fields: [
	{name: 'Abstract', type: 'string'},
	{name: 'AllowComments', type: 'boolean'},
	{name: 'Body', type: 'string'},
	{name: 'Byline', type: 'string'},
	{name: 'Copyright', type: 'string'},
	{name: 'Edition', type: 'Date'},
	{name: 'ID', type: 'int'},
	{name: 'SubTitle', type: 'string'},
	{name: 'Title', type: 'string'},
	{name: 'URL', type: 'string'},
	{name: 'Version', type: 'Date'},
	
	],
		
	associations: [	        
	{type: 'hasMany', model: 'Image', name: 'Images'},
	{type: 'hasMany', model: 'Story', name: 'RelatedStories'},
	{type: 'hasMany', model: 'Video', name: 'Videos'},
	
	]
});
