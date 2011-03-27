# -*- encoding: utf-8 -*-
$:.push File.expand_path("../lib", __FILE__)
require "bishop/version"

Gem::Specification.new do |s|
  s.name        = "bishop"
  s.version     = Bishop::VERSION
  s.platform    = Gem::Platform::RUBY
  s.authors     = ["Benjamin Moss"]
  s.email       = ["ben@monkeyhouse.ca"]
  s.homepage    = ""
  s.summary     = %q{Generates Android ContentProviders from XML Schema.}
  s.description = %q{"The A2s always were a bit twitchy. That could never happen now with our behavioral inhibitors. It is impossible for me to harm or by omission of action, allow to be harmed, a human being."}

  s.rubyforge_project = "bishop"
  
  # production
  s.add_dependency('nokogiri')
  s.add_dependency('trollop')
  # for the inflector/pluralizer
  s.add_dependency('activesupport')
  
  # development/testing
  s.add_development_dependency('rspec')

  s.files         = `git ls-files`.split("\n")
  s.test_files    = `git ls-files -- {test,spec,features}/*`.split("\n")
  s.executables   = `git ls-files -- bin/*`.split("\n").map{ |f| File.basename(f) }
  s.require_paths = ["lib"]
end
