module Bishop
  module Sencha
    class SenchaHammer

      def initialize(options)
        @xsd_dir = File.expand_path(File.dirname(options[:file]))
        @output_folder = options[:output]
        @sencha_template = ERB.new(File.read(File.join( File.dirname(__FILE__), '../../../templates/model.sencha.js.erb')))
        @pattern_map = PatternMap.new
      end    

      def generate( xsd_types )
        xsd_types.each do |xsd_type|
          next if xsd_type[:name] =~ /^ArrayOf/
        
          puts xsd_type[:name]
          # create a java class
          m = Model.new( xsd_type[:name] )
          m.fields = xsd_type[:fields].collect do |xsd_field|
            java_field = JavaField.new( xsd_field[:name] )

            java_field.type = get_type(xsd_field[:type])
            java_field.minOccurs = xsd_field[:minOccurs]
            java_field.nillable = xsd_field[:nillable]

            java_field.is_primitive = m.is_primitive( java_field.type )

            java_field
          end
        
          # set the type field for the template
          @type = m

          # render the templates and write them out
          write_file( "sencha.models.#{xsd_type[:name].downcase}", @sencha_template.result(get_binding) )
        
        end
      end
      
      def get_type( qualifiedTypeName )
        ns, typeName = qualifiedTypeName.split(':')
        @pattern_map.replace(ns, typeName)
      end
    
      def write_file( filename, contents )
        filename = filename.gsub('.','/')
        output_file = "#{@output_folder}/#{filename}.js"
        dir = File.dirname(output_file)

        FileUtils.mkdir_p dir

        puts "writing #{output_file}"
        File.open(output_file,'w') do |f|
          f.write(contents);
        end
      end

      def get_binding
        binding
      end
    end
  end
end
