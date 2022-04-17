/**
 * SmartCGMS - continuous glucose monitoring and controlling framework
 * https://diabetes.zcu.cz/
 *
 * Copyright (c) since 2018 University of West Bohemia.
 *
 * Contact:
 * diabetes@mail.kiv.zcu.cz
 * Medical Informatics, Department of Computer Science and Engineering
 * Faculty of Applied Sciences, University of West Bohemia
 * Univerzitni 8
 * 301 00, Pilsen
 * 
 * 
 * Purpose of this software:
 * This software is intended to demonstrate work of the diabetes.zcu.cz research
 * group to other scientists, to complement our published papers. It is strictly
 * prohibited to use this software for diagnosis or treatment of any medical condition,
 * without obtaining all required approvals from respective regulatory bodies.
 *
 * Especially, a diabetic patient is warned that unauthorized use of this software
 * may result into severe injure, including death.
 *
 *
 * Licensing terms:
 * Unless required by applicable law or agreed to in writing, software
 * distributed under these license terms is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * a) For non-profit, academic research, this software is available under the
 *      GPLv3 license.
 * b) For any other use, especially commercial use, you must contact us and
 *       obtain specific terms and conditions for the use of the software.
 * c) When publishing work with results obtained using this software, you agree to cite the following paper:
 *       Tomas Koutny and Martin Ubl, "Parallel software architecture for the next generation of glucose
 *       monitoring", Procedia Computer Science, Volume 141C, pp. 279-286, 2018
 */

#pragma once

#define TrackLeaks	
	//Sometimes, e.g. SaveFileDialog with Qt causes hangup

#ifdef TrackLeaks

	#define prefer_vld
		//Visual Leak Detector - vld.codeplex.com
		//This will cause problems, if a dll enabling VLD is loaded in a thread that does not finish as the last one.


	#if defined(_MSC_VER) && defined(_DEBUG)
		#ifdef prefer_vld
			#include <vld.h>
			//#include "../../../Third Party/Visual Leak Detector/include/vld.h"
		#else

			//The Thread Building Blocks has to include prior redefining new
			#ifdef TBB_USE_DEBUG
				#include "tbb/tbb.h"
				#include "tbb/task.h"
				#include "tbb/atomic.h"
			#endif

			#include <crtdbg.h>
			#define _CRTDBG_MAP_ALLOC
			#define DEBUG_CLIENTBLOCK   new( _CLIENT_BLOCK, __FILE__, __LINE__)
			#define new DEBUG_CLIENTBLOCK
		#endif	
	#endif

#endif

#include <sstream>


#include <iostream>
#include <fstream>	

#define overridedefdprintf

#if defined(_MSC_VER) && !defined(overridedefdprintf)
	#include <Dbgeng.h>	//provides dprintf
#else

#ifdef _WIN32
	#include <Windows.h>
#else
	#define OutputDebugStringA(x) (void)(x) // to avoid "unused variable" warning
	#define OutputDebugStringW(x) (void)(x)
	#define swprintf_s swprintf
	#define sprintf_s(x, y, z, a)
#endif

	template <typename... Args>
	void dprintf(char *format, Args... args) {
	   const size_t bufsize=2048;
	   char buf[bufsize];
	   sprintf_s(buf, bufsize, format, args...);
	   OutputDebugStringA(buf); 
	}

	template <typename... Args>
	void dprintf(wchar_t *format, Args... args) {
	   const size_t bufsize=2048;
	   wchar_t buf[bufsize];
	   swprintf_s(buf, bufsize, format, args...);
	   OutputDebugStringW(buf); 
	}

	template <typename T>
	void dprintf(T arg) {
		std::stringstream stream;
		stream << arg;		
		OutputDebugStringA(stream.str().c_str());
	}

#endif