Based on: #64

Compiling and running openbw on linux with client support:

```bash
# Download build deps
sudo apt install cmake libsdl2-dev libsdl2-mixer-dev #for ubuntu

# Build openbw with client support
git clone https://github.com/basil-ladder/openbw
git clone -b linux-client-support https://github.com/basil-ladder/bwapi
cd bwapi
mkdir build
cd build
cmake .. -DCMAKE_BUILD_TYPE=Release -DOPENBW_DIR=../../openbw -DOPENBW_ENABLE_UI=1
make -j4

# Download runtime deps
curl http://www.cs.mun.ca/~dchurchill/starcraftaicomp/files/Starcraft_1161.zip -o starcraft.zip
unzip starcraft.zip patch_rt.mpq BROODAT.MPQ STARDAT.MPQ
mv patch_rt.mpq Patch_rt.mpq && mv BROODAT.MPQ BrooDat.mpq && mv STARDAT.MPQ StarDat.mpq

# Run openbw using the map: Destination 1.1.scx
unzip starcraft.zip "maps/BroodWar/ICCup/ICCup Destination 1.1.scx"

BWAPI_CONFIG_AUTO_MENU__RACE=Terran BWAPI_CONFIG_AUTO_MENU__MAP="maps/BroodWar/ICCup/ICCup Destination 1.1.scx" ./bin/BWAPILauncher
```

Compiling and running openbw on macos with client support (not working):

```bash
brew install cmake sdl2 sdl2_mixer gcc

cmake .. -D CMAKE_C_COMPILER=gcc-11 -D CMAKE_CXX_COMPILER=g++-11 -DCMAKE_BUILD_TYPE=Release -DOPENBW_DIR=../../openbw -DOPENBW_ENABLE_UI=1
make -j4 
```