module Bishop
  class Type
    attr_accessor :name, :fields, :primitive_fields, :complex_fields

    def initialize()
      @fields = []
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
