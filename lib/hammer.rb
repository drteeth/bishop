class Hammer
  
  def initialize
    @tns = "http://appserver.canada.com/SouthPARC/"
    @xs = "http://www.w3.org/2001/XMLSchema"
    
    
    @map = {
      
      # :sql => {
      #   'xs' => [
      #     { :pattern => /ArrayOf(\w+)/, :substition => "$1_id" },
      #     { :pattern => /(\w+)/,  :substition => "$1" },
      #   ],
      #   'tns' =>[
      #     { :pattern => /xs:boolean/, :substition => "Boolean" },
      #           { :pattern => /xs:string/, :substition => "String" },
      #           { :pattern => /xs:dateTime/, :substition => "Date" },
      #           { :pattern => /xs:anyURI/, :substition => "Uri" },
      #   ],
      #},
    }
    
    
  end
  
  def load(file,output_namespace)
    @doc = Nokogiri::XML(File.open(file))
    @out_ns = output_namespace
    do_types
  end
  
  def do_types
    @complexTypes = @doc.xpath("//xs:complexType", 'xs' => @xs )
    
    @complexTypes.each do |t|
      
      type = Type.new
      type.name = t['name']
      
      @elements = t.xpath("xs:sequence/xs:element", 'xs' => @xs )
      
      @elements.each do |f|
        #puts "\t#{field['name']} #{field['type']}"        
        
        field = Field.new         
        field.name = f['name']
        field.type = f['type']
        field.minOccurs = f['minOccurs']
        field.nillable = f['nillable']
              
        type.fields << field
          
      end

      hammer = JavaHammer.new(@out_ns)
      hammer.drop_on( type )

    end
  end

end