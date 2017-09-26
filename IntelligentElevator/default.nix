{ stdenv, openjdk, ant }:

stdenv.mkDerivation {
  name = "hkbuddt";
  version = "0.1.0.0";
  src = ./.;
  buildInputs = [ openjdk ant ];
  buildPhase = "ant";
}
