module Bishop
  class PatternMap
    def initialize
      @patterns = {}

      @patterns[:tns] = []
      @patterns[:tns] << Pattern.new(/ArrayOf([a-zA-Z]+)/, "ArrayList<%s>", :interpolate)
      @patterns[:tns] << Pattern.new(/(\w+)/, "%s", :interplolate)

      @patterns[:xs] = []
      @patterns[:xs] << Pattern.new(/boolean/, "Boolean")
      @patterns[:xs] << Pattern.new(/string/, "String")
      @patterns[:xs] << Pattern.new(/int/, "Integer")
      @patterns[:xs] << Pattern.new(/dateTime/, "Date")
      @patterns[:xs] << Pattern.new(/anyURI/, "String")

      @patterns[:sql] = []
      @patterns[:sql] << Pattern.new(/String/, "Text")

      @patterns[:java_primitives] = []
      @patterns[:java_primitives] << Pattern.new(/Integer/, "int")
      @patterns[:java_primitives] << Pattern.new(/Boolean/, "String")
      @patterns[:java_primitives] << Pattern.new(/Date/, "long")
    end

    def replace(namespace, typeName)
      @patterns[namespace.to_sym].each do |matcher|
        if typeName =~ matcher.pattern
          return matcher.substitute($1)
        end
      end
      
      nil
    end

    def replace_all(namespace, value)
      @patterns[namespace.to_sym].each do |matcher|
        if typeName =~ matcher.pattern
          value = matcher.substitute($1)
        end
      end

      value
    end
  end
end
