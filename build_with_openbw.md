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
git clone --depth 1 https://github.com/dgant/mpqs
cp mpqs/patch_rt.mpq Patch_rt.mpq && cp mpqs/BROODAT.MPQ BrooDat.mpq && cp mpqs/STARDAT.MPQ StarDat.mpq

# Run openbw using the map: (2)Destination.scx
BWAPI_CONFIG_AUTO_MENU__RACE=Terran BWAPI_CONFIG_AUTO_MENU__MAP="it/(2)Destination.scx" ./bin/BWAPILauncher
```

Compiling and running openbw on macos with client support:

```bash
# Download build deps
brew install cmake sdl2 sdl2_mixer

# Build openbw with client support
git clone https://github.com/basil-ladder/openbw
git clone -b linux-client-support https://github.com/basil-ladder/bwapi
cd bwapi
mkdir build
cd build
cmake .. -DCMAKE_BUILD_TYPE=Release -DOPENBW_DIR=../../openbw -DOPENBW_ENABLE_UI=1 -DCMAKE_POLICY_VERSION_MINIMUM=3.5
make -j4

# Download runtime deps
git clone --depth 1 https://github.com/dgant/mpqs
cp mpqs/patch_rt.mpq Patch_rt.mpq && cp mpqs/BROODAT.MPQ BrooDat.mpq && cp mpqs/STARDAT.MPQ StarDat.mpq

# Run openbw using the map: (2)Destination.scx
BWAPI_CONFIG_AUTO_MENU__RACE=Terran BWAPI_CONFIG_AUTO_MENU__MAP="it/(2)Destination.scx" OPENBW_ENABLE_UI=1 ./bin/BWAPILauncher
```
