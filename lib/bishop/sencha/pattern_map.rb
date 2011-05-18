module Bishop
  module Sencha
    class PatternMap
      def initialize
        @patterns = {}

        @patterns[:tns] = []
        @patterns[:tns] << Pattern.new(/ArrayOf([a-zA-Z]+)/, "%s", :interpolate)
        @patterns[:tns] << Pattern.new(/(\w+)/, "%s", :interplolate)

        @patterns[:xs] = []
        @patterns[:xs] << Pattern.new(/boolean/, "boolean")
        @patterns[:xs] << Pattern.new(/string/, "string")
        @patterns[:xs] << Pattern.new(/int/, "int")
        @patterns[:xs] << Pattern.new(/dateTime/, "Date")
        @patterns[:xs] << Pattern.new(/anyURI/, "string")
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
end