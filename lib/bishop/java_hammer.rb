module Bishop
  class JavaHammer

    def initialize(options)
      @xsd_dir = File.expand_path(File.dirname(options[:file]))
      @namespace = options[:package]
      @output_folder = options[:output]

      @template = ERB.new(File.read(File.join( File.dirname(__FILE__), '../../templates/content-provider.java.erb')))

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
          { :pattern => /string/, :substition => "String" },
          { :pattern => /int/, :substition => "Integer" },
          { :pattern => /dateTime/, :substition => "Date" },
          { :pattern => /anyURI/, :substition => "String" },
        ],
      }

      @sql_map = {
        "String" => "Text"
      }

    end    

    def generate( xsd_types )
      xsd_types.each do |xsd_type|
        if xsd_type[:name] =~ /^ArrayOf/
          # don't generate ArrayOfTypes classes, 
          # the xsd parser makes references to the into ArrayList<Type>
          next 
        end
        
        # create a java class
        java_class = JavaClass.new( xsd_type[:name] )
        java_class.fields = xsd_type.fields.collect do |xsd_field|
          java_field = JavaField.new( xsd_field[:name] )
          
          java_field.type = get_java_type_name( xsd_field[:type] )
          java_field.minOccurs = xsd_field[:minOccurs]
          java_field.nillable = xsd_field[:nillable]

          java_field.is_primitive = is_primitive_type( java_field.type )
          java_field.sql_type = get_sql_type( java_field.type )
          
          java_field
        end 
        
        # TODO create an sql table
        # sql_table = SqlTable.new( sql_type( xsd_type.type ) )
        
        # render the template
        java_class_file = @template.result(get_binding)

        # write it out
        write_file( java_class.name )
        
      end
    end
    
    def write_file( filename )
      path = @namespace.gsub('.','/')
      dir = "#{@output_folder}#{path}/"

      FileUtils.mkdir_p dir

      File.open("#{dir}#{filename}.java",'w') do |f|
        f.write(v);
      end
    end

    def create_id_field()
      # add ID field
      id = JavaField.new
      id.name = "_ID"
      id.type = "xs:int"
      id.minOccurs = "1";
      id.nillable = "false"
      id.is_primitive = true
      id.java_type = "int"
      id.col_name = "_id"
      id
    end

    def is_primitive_type( typeName )
      @primitives.include?( typeName )
    end

    def get_sql_type( java_type )
      @sql_map[java_type] || java_type
    end

    def get_java_type_name( xsd_type_name )
      ns, name = xsd_type_name.split(':')

      java_type_name = nil
      
      @map[ns].each do |matcher|
        if name =~ matcher[:pattern]
          java_type_name = ( matcher[:substition] % $1 ) || matcher[:substition]
          break
        end
      end

      java_type_name
    end

    def get_binding
      binding
    end

  end
end
