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
      
      #pluralize(1, 'person')
      # => 1 person

      #pluralize(2, 'person')
      # => 2 people

      #pluralize(3, 'person', 'users')
      # => 3 users

      #pluralize(0, 'person')
      # => 0 people
    end
  end
end
