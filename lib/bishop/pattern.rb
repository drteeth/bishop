module Bishop
  class Pattern
    attr_reader :pattern, :substitution
    
    def initialize(pattern, substitition)
      @pattern = pattern
      @substitition = substitition
    end

    def substitute(value)
      @substitition % value
    end
  end
end
