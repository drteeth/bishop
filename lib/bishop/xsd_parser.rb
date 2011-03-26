module Bishop
  class XsdParser

    def initialize
      @xs = "http://www.w3.org/2001/XMLSchema"
    end

    def parse( file )
      schema = Nokogiri::XML(File.open(file))      
      types( schema )
    end

    def types( schema )
      schema.xpath("//xs:complexType", 'xs' => @xs ).map do |t|
        {
          :name => t['name'],
          :fields => fields(t),
        }
      end
    end
    
    def fields( type_node )
      type_node.xpath("xs:sequence/xs:element", 'xs' => @xs ).map do |f|
        {
          :name => f['name'],
          :type => f['type'],
          :minOccurs => f['minOccurs'],
          :nillable => f['nillable'],
        }
      end
    end
  end
end