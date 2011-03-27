module Bishop
  class JavaHammer

    def initialize(xsd_dir, namespace)
      @xsd_dir = xsd_dir
      @namespace = namespace
      @primitives = %w( Boolean String int Integer long Long Date )

      puts "LOUD LOVE!!!!!!!!!!!!!!!"
      
      @pattern_map = PatternMap.new
      # @map = 
      # {
      #   'tns' => 
      #   [
      #     { :pattern => /ArrayOf([a-zA-Z]+)/, :substition => "ArrayList<%s>" },
      #     { :pattern => /(\w+)/,  :substition => "%s" }, 
      #   ],
      #   'xs' =>
      #   [
      #     { :pattern => /boolean/, :substition => "Boolean" },
      #     { :pattern => /string/, :substition => "String" },
      #     { :pattern => /int/, :substition => "Integer" },
      #     { :pattern => /dateTime/, :substition => "Date" },
      #     { :pattern => /anyURI/, :substition => "String" },
      #   ],
      # }

      # @sql_map = {
      #   "String" => "Text"
      # }

    end    

    def drop_on( type )
      return if type.name =~ /^ArrayOf/
      
      @template = ERB.new(File.read(File.join( File.dirname(__FILE__), '../../templates/content-provider.java.erb')))
      @type = generate_class(type)
      v = @template.result(get_binding)

      path = @namespace.gsub('.','/')
      dir = "./generated/#{path}/"

      FileUtils.mkdir_p dir

      File.open("#{dir}#{@type.name}.java",'w') do |f|
        f.write(v);
      end
    end

    def generate_class( type )

      type.fields.each do |f|
        f.java_type = swap_type(f.type)
        f.col_name = f.name.downcase
        f.is_primitive = primitive_type? f.java_type
        f.sql_type = get_sql_type( f.java_type )
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

      type.primitive_fields = type.fields.reject { |f| (primitive_type?( f.java_type )==false) }
      type.complex_fields = type.fields.reject { |f| primitive_type?( f.java_type ) }

      type

    end

    def primitive_type?( typeName )
      @primitives.include?( typeName )
    end

    def kludge( string )
      string.gsub("getInteger", "getInt").gsub("getBoolean","getInt")
    end

    def get_sql_type( java_type )
      # @sql_map[java_type] || java_type
      @pattern_map.replace(:sql, java_type) || java_type
    end

    def swap_type( qualifiedTypeName )
      ns, typeName = qualifiedTypeName.split(':')

      @pattern_map.replace(ns, typeName)
      #javaTypeName = nil

      # for each pattern in the namespace
      # @map[ns].each do |matcher|
      #   if typeName =~ matcher[:pattern]
      #     # javaTypeName = (matcher[:substition] % $1 ) || matcher[:substition]
      #     javaTypeName = matcher[:substition] % $1
      #     break
      #   end
      # end

      #puts "\t\t#{javaTypeName}"

      #javaTypeName
    end

    def get_binding
      binding
    end

  end
end
