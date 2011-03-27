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

      @patterns[:java] = []
      @patterns[:java] << Pattern.new(/getInteger/, "getInt")
      @patterns[:java] << Pattern.new(/getBoolean/, "getInt")
    end

    def replace(namespace, typeName)
      puts "replace: #{namespace}:#{typeName}"
      @patterns[namespace.to_sym].each do |matcher|
        if typeName =~ matcher.pattern
          val = matcher.substitute($1)
          puts "replaced with: #{val}"
          return val
        end
      end
      puts "returning nil for replace #{namespace}:#{typeName}"
      nil
    end

    def replace_all(namespace, value)
      puts "replace_all: #{namespace}:#{value}"
      @patterns[namespace.to_sym].each do |matcher|
        if typeName =~ matcher.pattern
          value = matcher.substitute($1)
          puts "value is now: #{value}"
        end
      end

      value
    end
  end
end