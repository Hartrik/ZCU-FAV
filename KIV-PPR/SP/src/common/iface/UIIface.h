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

#include "FilterIface.h"
#include "../rtl/guid.h"

namespace glucose {

	//Units used for glucose levels 
	enum class TUnits : int8_t {
		MmolPerL = 0, //mmol/l
		MgPerDl,	//mg/dl
		DgPerDl,	//dg/dl
	};

	enum class NFilter_Flags : uint8_t {
		Synchronous	= 1 << 0,

		None = 0
	};

	using TFilter_Flags = std::underlying_type<NFilter_Flags>::type;

	inline NFilter_Flags operator|(const NFilter_Flags lhs, const NFilter_Flags rhs) {
		return static_cast<NFilter_Flags>(static_cast<TFilter_Flags>(lhs) | static_cast<TFilter_Flags>(rhs));
	}

	inline NFilter_Flags operator&(const NFilter_Flags lhs, const NFilter_Flags rhs) {
		return static_cast<NFilter_Flags>(static_cast<TFilter_Flags>(lhs) & static_cast<TFilter_Flags>(rhs));
	}

	struct TFilter_Descriptor {
		const GUID id;
		const NFilter_Flags flags;
		const wchar_t *description;
		const size_t parameters_count;	//can be zero
		const NParameter_Type* parameter_type;
		const wchar_t** ui_parameter_name;
		const wchar_t** config_parameter_name;
		const wchar_t** ui_parameter_tooltip; // always the same size as other parameter fields, nullptr where no tooltip needed
	};
	
	constexpr TFilter_Descriptor Null_Filter_Descriptor = { Invalid_GUID, NFilter_Flags::None, nullptr, 0, nullptr, nullptr, nullptr, nullptr };

	struct TMetric_Descriptor {
		const GUID id;
		const wchar_t *description;
	};


	/* any model parameter must be expressed with double
	   these constant express how to interpret that double
	*/
	enum class NModel_Parameter_Value : int8_t {
		mptDouble,
		mptTime,
		mptBool
	};

	struct TModel_Descriptor {
		const GUID id;
		const wchar_t *description;
		const wchar_t *db_table_name;

		const size_t number_of_parameters;				//cannot be zero
		const NModel_Parameter_Value *parameter_types;	//array of mptConstants
		const wchar_t **parameter_ui_names;
		const wchar_t **parameter_db_column_names;

		const double *lower_bound;
		const double *default_values;
		const double *upper_bound;

		//signals which can be calculated using this model
		const size_t number_of_calculated_signals;	//cannot be zero
		const GUID* calculated_signal_ids;
		const wchar_t **calculated_signal_names;
		const GUID* reference_signal_ids;
	};

	constexpr TModel_Descriptor Null_Model_Descriptor = { Invalid_GUID, nullptr, nullptr, 0, nullptr, nullptr, nullptr, nullptr, nullptr, nullptr, 0, nullptr, nullptr, nullptr };

	struct TSolver_Descriptor {
		const GUID id;
		const wchar_t *description;

		const bool specialized;	//if false, can be applied to any model
								//if true, the following fields apply
		const size_t specialized_count;		//can be zero with specialized == false, usually 1
		const GUID *specialized_models;		//array of models, whose parameters the solver can solve
	};

	constexpr TSolver_Descriptor Null_Solver_Descriptor = { Invalid_GUID, nullptr, false, 0, nullptr };

	struct TApprox_Descriptor {
		const GUID id;
		const wchar_t *description;
		const size_t parameters_count;	//can be zero
		const wchar_t** ui_parameter_name;
		const wchar_t** config_parameter_name;
	};

	constexpr TApprox_Descriptor Null_Approx_Descriptor = { Invalid_GUID, nullptr, 0, nullptr, nullptr };

	struct TDevice_Driver_Descriptor {
		const GUID id;
		const wchar_t *description;
		const size_t parameters_count;	//can be zero
		const NParameter_Type* parameter_type;
		const wchar_t** ui_parameter_name;
		const wchar_t** config_parameter_name;
	};

	constexpr TDevice_Driver_Descriptor Null_Device_Driver_Descriptor = { Invalid_GUID, nullptr, 0, nullptr, nullptr, nullptr };

	using TGet_Filter_Descriptors = HRESULT(IfaceCalling*)(TFilter_Descriptor **begin, TFilter_Descriptor **end);
	using TGet_Metric_Descriptors = HRESULT(IfaceCalling*)(TMetric_Descriptor **begin, TMetric_Descriptor **end);
	using TGet_Model_Descriptors = HRESULT(IfaceCalling*)(TModel_Descriptor **begin, TModel_Descriptor **end);
	using TGet_Solver_Descriptors = HRESULT(IfaceCalling*)(TSolver_Descriptor **begin, TSolver_Descriptor **end);
	using TGet_Approx_Descriptors = HRESULT(IfaceCalling*)(TApprox_Descriptor **begin, TApprox_Descriptor **end);
	using TGet_Device_Driver_Descriptors = HRESULT(IfaceCalling*)(TDevice_Driver_Descriptor **begin, TDevice_Driver_Descriptor **end);

	using TAdd_Filters = HRESULT(IfaceCalling *)(const glucose::TFilter_Descriptor *begin, const glucose::TFilter_Descriptor *end, const glucose::TCreate_Filter create_filter);
}
