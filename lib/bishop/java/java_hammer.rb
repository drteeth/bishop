module Bishop
  module Java
    class JavaHammer

      def initialize(options)
        @xsd_dir = File.expand_path(File.dirname(options[:file]))
        @namespace = options[:package]
        @output_folder = options[:output]
        @provider_template = ERB.new(File.read(File.join( File.dirname(__FILE__), '../../../templates/content-provider.java.erb')))
        @dto_template = ERB.new(File.read(File.join( File.dirname(__FILE__), '../../../templates/dto.java.erb')))
        @test_template = ERB.new(File.read(File.join( File.dirname(__FILE__), '../../../templates/test.java.erb')))
        @pattern_map = PatternMap.new
      end

      def generate( xsd_types )

        xsd_types.each do |xsd_type|
          next if xsd_type[:name] =~ /^ArrayOf/

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

            java_field.col_name = "_id" if xsd_field[:name].downcase == "id"

            java_field
          end

          java_class.fields << create_id_field unless java_class.has_id?
          java_class.fields << create_created_field
          java_class.fields << create_modified_field

          # super hack for android
          # _id is the magic id column name
          # hammer the id field to look like this

          # TODO create an sql table
          # sql_table = SqlTable.new( sql_type( xsd_type.type ) )

          # set the type field for the template
          @type = java_class

          # render the templates and write them out
          write_file( "#{@namespace}.provider.#{java_class.name}Provider", @provider_template.result(get_binding) )
          write_file( "#{@namespace}.model.#{java_class.name}", @dto_template.result(get_binding) )
          write_file( "#{@namespace}.provider_tests.#{java_class.name}ProviderTestBase", @test_template.result(get_binding) )

        end
      end

      def write_file( filename, contents )
        filename = filename.gsub('.','/')
        output_file = "#{@output_folder}/#{filename}.java"
        dir = File.dirname(output_file)

        FileUtils.mkdir_p dir

        puts "writing #{output_file}"
        File.open(output_file,'w') do |f|
          f.write(contents);
        end
      end

      def create_id_field
        # add ID field
        id = JavaField.new( 'ID' )
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
        id.type = "Long"
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
        id.type = "Long"
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
end
