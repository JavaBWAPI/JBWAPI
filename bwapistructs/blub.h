#pragma once

#include "BWAPI/Client/GameData.h"

// Pull in GameData
int dummy() {
	return sizeof(BWAPI::GameData);
}