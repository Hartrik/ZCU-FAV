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

#include "DebugHelper.h"

#if defined(_MSC_VER) && defined(_DEBUG)
    #include <wchar.h>
	#include <stdlib.h>	
	#include <Windows.h>

    extern "C" char __ImageBase;
#endif

	

class CMemory_Leak_Tracker {
	//just a class-helper that correctly catches memory allocated with global const variable
public:
	CMemory_Leak_Tracker();
	~CMemory_Leak_Tracker();
};

extern const CMemory_Leak_Tracker Memory_Leak_Tracker;

	

CMemory_Leak_Tracker::CMemory_Leak_Tracker() {
	#ifdef TrackLeaks
		#if defined(_MSC_VER) && defined(_DEBUG)
			#ifdef prefer_vld
				VLDEnable();
			#else
				_CrtSetDbgFlag(_CRTDBG_ALLOC_MEM_DF | _CRTDBG_LEAK_CHECK_DF | _CRTDBG_CHECK_ALWAYS_DF);
			#endif
		#endif
	#endif
}

CMemory_Leak_Tracker::~CMemory_Leak_Tracker() {
	#ifdef TrackLeaks
		#if defined(_MSC_VER) && defined(_DEBUG)
	
			//wchar_t *fileName = (wchar_t*) malloc(1024*sizeof(wchar_t));
			//wchar_t *buf = (wchar_t*) malloc(2048*sizeof(wchar_t));
			//can't be on the heap, otherwise they will be reported as leaks
			wchar_t fileName[1024];
			wchar_t buf[2048];
			GetModuleFileNameW(((HINSTANCE)&__ImageBase), fileName, 1024);

			swprintf_s(buf, 2048, L"========== Dumping leaks for: %s ==========\n", fileName);
			OutputDebugStringW(buf);

			#ifdef prefer_vld
				VLDReportLeaks();
			#else
				_CrtDumpMemoryLeaks();
			#endif

			swprintf_s(buf, 2048, L"========== Leaks dumped for: %s ==========\n", fileName);
			OutputDebugStringW(buf);


		#endif
	#endif
}

const CMemory_Leak_Tracker Memory_Leak_Tracker;