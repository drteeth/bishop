module Bishop
  class Type
    attr_accessor :name, :fields, :primitive_fields, :complex_fields

    def initialize(name)
      @fields = []
      @name = name
    end

    def build(complex_type, ns)
      elements = complex_type.xpath("xs:sequence/xs:element", 'xs' => ns)

      elements.each do |field|
        @fields << Field.new(field)
      end
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
