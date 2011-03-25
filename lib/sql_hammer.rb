class SqlHammer
  
  def initialize(namespace)
    @namespace = namespace
    @primitives = %W( Boolean Text Integer Long  )

    @map = 
    {
      'tns' => 
      [
        { :pattern => /ArrayOf([a-zA-Z]+)/, :substition => "ArrayList<%s>" },
        { :pattern => /(\w+)/,  :substition => "%s" },
      ],
      'xs' =>
      [
        { :pattern => /boolean/, :substition => "Boolean" },
        { :pattern => /string/, :substition => "Text" },
        { :pattern => /int/, :substition => "Integer" },
        { :pattern => /dateTime/, :substition => "DateTime" },
        { :pattern => /anyURI/, :substition => "String" },
      ],
    }
    
    
  end    
  
  def drop_on( type )
    @template = ERB.new(File.read('content-provider.java.erb'))

     if type.name =~ /^ArrayOf/
       return
     end

     @type = generate_class(type)
     v = @template.result(get_binding)

     path = @namespace.gsub('.','/')
     dir = "./#{path}/"

     FileUtils.mkdir_p dir

     File.open("#{dir}#{@type.name}.java",'w') do |f|
       f.write(v);
     end

   end

   def generate_class( type )

     type.fields.each do |f|
       f.java_type = swap_type(f.type)
       f.col_name = f.name.downcase
       f.is_primitive = is_primitive_type f.java_type
     end
     
     # add ID field
     id = Field.new
     id.name = "_ID"
     id.type = "xs:int"
     id.minOccurs = "1";
     id.nillable = "false"
     id.is_primitive = true
     id.java_type = "int"
     id.col_name = "_id"
     type.fields << id

     type.primitive_fields = type.fields.reject { |f| (is_primitive_type( f.java_type )==false) }
     type.complex_fields = type.fields.reject { |f| is_primitive_type( f.java_type ) }

     type

   end

   def is_primitive_type( typeName )
     @primitives.include?( typeName )
   end

   def swap_type( qualifiedTypeName )
     ns, typeName = qualifiedTypeName.split(':')

     javaTypeName = nil
     @map[ns].each do |matcher|
       if typeName =~ matcher[:pattern]
         javaTypeName = (matcher[:substition] % $1 ) || matcher[:substition]
         break
       end
     end

     #puts "\t\t#{javaTypeName}"

     javaTypeName
   end
   
   def get_binding
     binding
   end
  
end