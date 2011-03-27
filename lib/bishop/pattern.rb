module Bishop
  class Pattern
    attr_reader :pattern, :substitution

    # action can be :replace or :interpolate
    def initialize(pattern, substitition, action=:replace)
      @pattern = pattern
      @substitition = substitition
      @action = action
    end

    def substitute(value)
      if @action == :interpolate
        @substitition % value
      else
        @substitition
      end
    end
  end
end
