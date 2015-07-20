#!/usr/bin/perl -w

use Test::More;
use strict;
use File::Spec;

sub _write_utf8_file
{
    my ($out_path, $contents) = @_;

    open my $out_fh, '>:encoding(utf8)', $out_path
        or die "Cannot open '$out_path' for writing - $!";

    print {$out_fh} $contents;

    close($out_fh);

    return;
}

# test graphviz (dot) file input => ASCII output
# and back to as_txt() again

BEGIN
   {
   plan tests => 140;
   chdir 't' if -d 't';
   use lib '../lib';
   use_ok ("Graph::Easy") or die($@);
   use_ok ("Graph::Easy::Parser") or die($@);
   use_ok ("Graph::Easy::Parser::Graphviz") or die($@);
   };

my @warnings;

#############################################################################
# override the warn method to catch warnigs

{
 no warnings 'redefine';

 package Graph::Easy::Base;

 sub warn {
   my ($self,$msg) = @_;
   push @warnings, $msg;
 }

}

#############################################################################
# parser object

my $def_parser = Graph::Easy::Parser->new(debug => 0);

is (ref($def_parser), 'Graph::Easy::Parser');
is ($def_parser->error(), '', 'no error yet');

my $dot_parser = Graph::Easy::Parser::Graphviz->new(debug => 0);
is (ref($dot_parser), 'Graph::Easy::Parser::Graphviz');
is ($dot_parser->error(), '', 'no error yet');

my $dir = File::Spec->catdir('in','dot');

opendir DIR, $dir or die ("Cannot read dir '$dir': $!");
my @files = readdir(DIR); closedir(DIR);

opendir DIR, 'dot' or die ("Cannot read dir 'dot': $!");
push @files, readdir(DIR); closedir(DIR);

binmode (STDERR, ':utf8') or die ("Cannot do binmode(':utf8') on STDERR: $!");
binmode (STDOUT, ':utf8') or die ("Cannot do binmode(':utf8') on STDOUT: $!");

eval { require Test::Differences; };

foreach my $f (sort {
  $a =~ /^(\d+)/; my $a1 = $1 || '1';
  $b =~ /^(\d+)/; my $b1 = $1 || '1';
  $a1 <=> $b1 || $a cmp $b;
  } @files)
  {
  my $file = File::Spec->catfile($dir,$f);
  my $parser = $def_parser;
  if (!-f $file)
    {
    $file = File::Spec->catfile('dot',$f);
    next unless -f $file; 			# only files
    # for files in t/dot, we need to use the Graphviz parser as they
    # look like Graph::Easy text to the normal parser, which then fails
    $parser = $dot_parser;
    }

  next unless $f =~ /\.dot/;			# ignore anything else

  print "# at $f\n";
  my $txt = readfile($file);
  $parser->reset();
  my $graph = $parser->from_text($txt);		# reuse parser object

  $f =~ /^(\d+)/;
  my $nodes = $1;

  if (!defined $graph)
    {
    fail ("Graphviz input was invalid: " . $parser->error());
    next;
    }
  is (scalar $graph->nodes(), $nodes, "$nodes nodes");

  # for slow testing machines
  $graph->timeout(20);
  my $ascii = $graph->as_ascii();

  my $of = $f; $of =~ s/\.dot/\.txt/;
  my $out_path = File::Spec->catfile('out','dot',$of);
  my $out = readfile($out_path);
  $out =~ s/(^|\n)#[^# ]{2}.*\n//g;		# remove comments
  $out =~ s/\n\n\z/\n/mg;			# remove empty lines

# print "txt: $txt\n";
# print "ascii: $ascii\n";
# print "should: $out\n";

  if (!
      is ($ascii, $out, "from $f"))
  {
      if ($ENV{__SHLOMIF__UPDATE_ME})
      {
          _write_utf8_file($out_path, $ascii);
      }
      if (defined $Test::Differences::VERSION)
      {
          Test::Differences::eq_or_diff ($ascii, $out);
      }
      else
      {
          fail ("Test::Differences not installed");
      }
  }

  # if the txt output differes, read it in
  my $f_txt = File::Spec->catfile('txt','dot',$of);
  if (-f $f_txt)
    {
    $txt = readfile($f_txt);
    }

  $graph->debug(1);

  if (!
      is ($graph->as_txt(), $txt, "$f as_txt"))
  {
      if ($ENV{__SHLOMIF__UPDATE_ME})
      {
          _write_utf8_file($f_txt, scalar( $graph->as_txt() ));
      }
      if (defined $Test::Differences::VERSION)
      {
          Test::Differences::eq_or_diff ($graph->as_txt(), $txt);
      }
      else
      {
          fail ("Test::Differences not installed");
      }
  }

  # print a debug output
  my $debug = $ascii;
  $debug =~ s/\n/\n# /g;
  print "# Generated:\n#\n# $debug\n";
  }

# check that only the expected warnings were generated
use Data::Dumper;

print STDERR Dumper(\@warnings) unless
  is (scalar @warnings, 6, 'Got exactly 6 warnings');

my $i = 0;
for my $name (qw/bar foo pname fname bar brabble/)
  {
  like ($warnings[$i], qr/Ignoring unknown attribute '$name' for class/,
	"Got warning about $name");
  $i++;
  }

1;

sub readfile
  {
  my ($file) = @_;

  open my $FILE, $file or die ("Cannot read file $file: $!");
  binmode ($FILE, ':utf8') or die ("Cannot do binmode(':utf8') on $FILE: $!");
  local $/ = undef;				# slurp mode
  my $doc = <$FILE>;
  close $FILE;

  $doc;
  }
