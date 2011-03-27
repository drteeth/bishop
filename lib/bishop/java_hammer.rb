module Bishop
  class JavaHammer

    def initialize(options)
      @xsd_dir = File.expand_path(File.dirname(options[:file]))
      @namespace = options[:package]
      @output_folder = options[:output]
      @template = ERB.new(File.read(File.join( File.dirname(__FILE__), '../../templates/content-provider.java.erb')))
      @pattern_map = PatternMap.new
    end    

    def generate( xsd_types )
      xsd_types.each do |xsd_type|
        next if type.name =~ /^ArrayOf/
        
        puts xsd_type[:name]
        
        # create a java class
        java_class = JavaClass.new( xsd_type[:name] )
        java_class.fields = xsd_type[:fields].collect do |xsd_field|
          java_field = JavaField.new( xsd_field[:name] )
          
          java_field.type = get_java_type_name( xsd_field[:type] )
          java_field.minOccurs = xsd_field[:minOccurs]
          java_field.nillable = xsd_field[:nillable]

          java_field.is_primitive = java_class.is_primitive( java_field.type )
          java_field.sql_type = get_sql_type( java_field.type )
          
          java_field
        end
        
        java_class.fields << create_id_field
        java_class.fields << create_created_field
        java_class.fields << create_modified_field
        
        # TODO create an sql table
        # sql_table = SqlTable.new( sql_type( xsd_type.type ) )
        
        # set the type field for the template
        @type = java_class

        # render the template and write it out
        write_file( java_class.name, @template.result(get_binding) )
        
      end
    end
    
    def write_file( filename, contents )
      path = @namespace.gsub('.','/')
      dir = "#{@output_folder}#{path}/"
      output_file = "#{dir}#{filename}.java"

      FileUtils.mkdir_p dir

      puts "writing #{output_file}"
      File.open(output_file,'w') do |f|
        f.write(contents);
      end
    end

    def create_id_field()
      # add ID field
      id = JavaField.new( "_ID " )
      id.type = "Integer"
      id.minOccurs = "1";
      id.nillable = "false"
      id.is_primitive = true
      id.col_name = "_id"
      id.sql_type = "integer"
      id
    end
    
    def create_created_field()
      # add ID field
      id = JavaField.new( "Created" )
      id.type = "Integer"
      id.minOccurs = "1";
      id.nillable = "false"
      id.is_primitive = true
      id.col_name = "created"
      id.sql_type = "integer"
      id
    end
    
    def create_modified_field()
      # add ID field
      id = JavaField.new( "Modified" )
      id.type = "Integer"
      id.minOccurs = "1";
      id.nillable = "false"
      id.is_primitive = true
      id.col_name = "modified"
      id.sql_type = "integer"
      id
    end

     def get_sql_type( java_type )
      @pattern_map.replace(:sql, java_type) || java_type
    end

    def get_java_type_name( qualifiedTypeName )
      ns, typeName = qualifiedTypeName.split(':')
      @pattern_map.replace(ns, typeName)
    end

    def get_binding
      binding
    end
  end
end
