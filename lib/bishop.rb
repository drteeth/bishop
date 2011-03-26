require 'nokogiri'
require 'erb'
require 'fileutils'

require 'bishop/args'
require 'bishop/field'
require 'bishop/type'
require 'bishop/xsd_parser'
require 'bishop/java_hammer'

module Bishop
  DEBUG = true
  
  class Application
    def run
      args = Args.new
      args.parse

      if ! args.set? :file
        puts "usage: bishop -f <xsd>"
        exit(-1)
      end
      
      h = XsdParser.new
      h.parse(args.value(:file), "com.indusblue.providers")
    end
  end
end
