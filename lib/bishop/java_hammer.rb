module Bishop
  class JavaHammer

    def initialize(namespace)
      @namespace = namespace
      @primitives = %w( Boolean String int Integer long Long Date )

      @pattern_map = PatternMap.new
    end    

    def drop_on( type )
      return if type.name =~ /^ArrayOf/
      
      @template = ERB.new(File.read(File.join( File.dirname(__FILE__), '../../templates/content-provider.java.erb')))
      @type = generate_class(type)
      v = @template.result(get_binding)

      path = @namespace.gsub('.','/')
      dir = "./generated/#{path}/"

      FileUtils.mkdir_p(dir)

      File.open("#{dir}#{@type.name}.java",'w') do |f|
        f.write(v);
      end
    end

    def generate_class( type )
      massage_fields(type)
      
      # add ID field to type
      type.fields << create_id_field()
      # reject => where block is not true
      # delete_if where block is true
      type.primitive_fields = type.fields.reject { |f| (primitive_type?( f.java_type )==false) }
      type.complex_fields = type.fields.reject   { |f| primitive_type?( f.java_type ) }

      type
    end

    def massage_fields(type)
      type.fields.each do |f|
        f.java_type = swap_type(f.type)
        f.col_name = f.name.downcase
        f.is_primitive = primitive_type? f.java_type
        f.sql_type = get_sql_type( f.java_type )
      end
    end
    
    def create_id_field
      field = Field.new({
        'name' => "_ID",
        'type' => "xs:int",
        'minOccurs' => "1",
        'nillable' => "false"
      })
      
      field.is_primitive = true
      field.java_type = "int"
      field.col_name = "_id"

      field
    end
    
    def primitive_type?( typeName )
      @primitives.include?( typeName )
    end

    def kludge( string )
      @pattern_map.replace_all(:java, string)
    end

    def get_sql_type( java_type )
      @pattern_map.replace(:sql, java_type) || java_type
    end

    def swap_type( qualifiedTypeName )
      ns, typeName = qualifiedTypeName.split(':')
      @pattern_map.replace(ns, typeName)
    end

    def get_binding
      binding
    end
  end
end
