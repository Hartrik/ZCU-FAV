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

#include "../../common/iface/UIIface.h"
#include "../../common/rtl/hresult.h"


namespace pprsolver {
	//pro oba solvery si vygenerujte nove GUID
	constexpr GUID pprsolver_id_serial = { 0x2c6f2955, 0xedbf, 0x4984, { 0x81, 0x53, 0xe6, 0xcf, 0xf5, 0x55, 0x9a, 0xbd } };
	constexpr wchar_t pprsolver_desc_serial[] = L"Patrik Harag - SPO - Serial";
    constexpr GUID pprsolver_id_smp = { 0x2c6f2956, 0xedbf, 0x4984, { 0x84, 0x53, 0xe6, 0xcf, 0xf5, 0x55, 0x9a, 0xbd } };
	constexpr wchar_t pprsolver_desc_smp[] = L"Patrik Harag - SPO - SMP";
    constexpr GUID pprsolver_id_opencl = { 0x2c6f2957, 0xedbf, 0x4984, { 0x85, 0x53, 0xe6, 0xcf, 0xf5, 0x55, 0x9a, 0xbd } };
	constexpr wchar_t pprsolver_desc_opencl[] = L"Patrik Harag - SPO - OpenCL";
}

extern "C" HRESULT IfaceCalling do_get_solver_descriptors(glucose::TSolver_Descriptor **begin, glucose::TSolver_Descriptor **end);