{ supportedSystems ? ["x86_64-linux"] }:

with import <nixpkgs/pkgs/top-level/release-lib.nix> { inherit supportedSystems; };
{
  build = testOn supportedSystems (pkgs:
    pkgs.callPackage ./default.nix { });
}
