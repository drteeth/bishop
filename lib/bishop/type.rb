module Bishop
  class Type
    attr_accessor :name, :fields, :primitive_fields, :complex_fields

    def initialize(name)
      @fields = []
      @name = name
    end

    def build(complex_type)
      elements = complex_type.xpath("xs:sequence/xs:element", 'xs' => @xs)

      elements.each do |field|
        @fields << Field.new(field)
      end
      
      # @elements.each do |f|
        #   #puts "\t#{field['name']} #{field['type']}"        

        #   field = Field.new         
        #   field.name = f['name']
        #   field.type = f['type']
        #   field.minOccurs = f['minOccurs']
        #   field.nillable = f['nillable']

        #   type.fields << field
        #  end
    end

    def getter
      "get#{type}()"
    end

    def setter
      "set#{name}( #{type} #{} )"
    end

    def pluralize
      "#{name}s"
    end
  end
end
