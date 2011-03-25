require 'nokogiri'
require 'erb'
require 'fileutils'
require_relative 'lib/field'
require_relative 'lib/type'
require_relative 'lib/hammer'
require_relative 'lib/java_hammer'


h = Hammer.new
h.load("example.xsd", "com.indusblue.providers")
