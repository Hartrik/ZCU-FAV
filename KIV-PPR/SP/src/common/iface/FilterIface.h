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

#include <memory>
#include <string>
#include <vector>

#include "DeviceIface.h"
#include "SolverIface.h"

namespace glucose {

	class IEvent_Receiver : public virtual refcnt::IReferenced {
	public:
		//caller TAKES ownership of the received event and is responsible for freeing it
		//on receiving anything else but S_OK, any filter is supposed to terminate its Execute method
		virtual HRESULT IfaceCalling receive(IDevice_Event **event) = 0;
	};

	class IEvent_Sender : public virtual refcnt::IReferenced {
	public:
		// Pipe TAKES ownership of any nested reference-counted I-object so that send-caller is forbidden to call to release the nested objects
		virtual HRESULT IfaceCalling send(IDevice_Event *event) = 0;
	};


	using time_segment_id_container = refcnt::IVector_Container<int64_t>;

	enum class NParameter_Type : size_t {
		ptNull = 0,
		ptWChar_Container,			//IParameter_Container<wchar_t>
		ptSelect_Time_Segment_ID,	//alias for IParameter_Container<int64_t> that selects time segments id
		ptDouble,	
		ptRatTime,					//double interperted as the rattime
		ptInt64,
		ptBool,
		ptModel_Id,
		ptMetric_Id,
		ptSolver_Id,
		ptModel_Signal_Id,	// signal dependend on model selection
		ptSignal_Id,		// any signal available (measured, calculated)
		ptModel_Bounds,		// three parameter sets in one container - lower bound, default values, higher bound
		ptSubject_Id,		// int64_t, but with additional functionality in GUI
		ptDevice_Driver_Id,	// device driver GUID (pump, sensor, ..)
	};

	struct TFilter_Parameter {
		//data marshalling to enable inter-operability
		NParameter_Type type;
		refcnt::wstr_container *config_name;
		union {
			refcnt::wstr_container* wstr;		//ptWChar_Container
			time_segment_id_container* select_time_segment_id;
			double dbl;
			int64_t int64;
			uint8_t boolean;
			GUID guid;
			glucose::IModel_Parameter_Vector* parameters;
		};
	};

	class IFilter_Parameter : public virtual refcnt::IReferenced {
	public:
		//read-only
		virtual HRESULT IfaceCalling Get_Type(NParameter_Type *type) = 0;
		virtual HRESULT IfaceCalling Get_Config_Name(refcnt::wstr_container **config_name) = 0;	
		
		//read-write
		virtual HRESULT IfaceCalling Get_WChar_Container(refcnt::wstr_container **wstr) = 0;
		virtual HRESULT IfaceCalling Set_WChar_Container(refcnt::wstr_container *wstr) = 0;	

		virtual HRESULT IfaceCalling Get_Time_Segment_Id_Container(time_segment_id_container **ids) = 0;
		virtual HRESULT IfaceCalling Set_Time_Segment_Id_Container(time_segment_id_container *ids) = 0;

		virtual HRESULT IfaceCalling Get_Double(double *value) = 0;
		virtual HRESULT IfaceCalling Set_Double(const double value) = 0;

		virtual HRESULT IfaceCalling Get_Int64(int64_t *value) = 0;
		virtual HRESULT IfaceCalling Set_Int64(const int64_t value) = 0;

		virtual HRESULT IfaceCalling Get_Bool(uint8_t *boolean) = 0;
		virtual HRESULT IfaceCalling Set_Bool(const uint8_t boolean) = 0;

		virtual HRESULT IfaceCalling Get_GUID(GUID *id) = 0;
		virtual HRESULT IfaceCalling Set_GUID(const GUID *id) = 0;

		virtual HRESULT IfaceCalling Get_Model_Parameters(glucose::IModel_Parameter_Vector **parameters) = 0;
		virtual HRESULT IfaceCalling Set_Model_Parameters(glucose::IModel_Parameter_Vector *parameters) = 0;
	};

	constexpr TFilter_Parameter Null_Filter_Parameter = { NParameter_Type::ptNull, nullptr, { nullptr } };

	using IFilter_Configuration = refcnt::IVector_Container<glucose::IFilter_Parameter>;
	
	class IFilter_Configuration_Link : public virtual refcnt::IReferenced {
	public:
		virtual HRESULT IfaceCalling Get_Filter_Id(GUID *id) = 0;
		virtual HRESULT IfaceCalling Set_Configuration(IFilter_Configuration *configuration) = 0;
		virtual HRESULT IfaceCalling Get_Configuration(IFilter_Configuration **configuration) = 0;
	};

	using IFilter_Chain_Configuration = refcnt::IVector_Container<IFilter_Configuration_Link>;

	class IFilter : public virtual refcnt::IReferenced {
	public:
		//Set up filter configuration, possibly during its execution
		virtual HRESULT IfaceCalling Configure(IFilter_Configuration* configuration) = 0;

		//Executes the filter's control loop
		//Asynchronously executed filter runs in a dedicated thread
		//Synchronously executed filters runs repeatedly in a worker thread, each Execute ends oncee IPipe_Reader fails to return an event
		virtual HRESULT IfaceCalling Execute() = 0;
	};

	
	using TCreate_Filter = HRESULT(IfaceCalling *)(const GUID *id, IEvent_Receiver *input, IEvent_Sender *output, glucose::IFilter **filter);
	using TOn_Filter_Created = HRESULT(IfaceCalling *)(const void* data, glucose::IFilter *filter);

	class IFilter_Executor : public virtual refcnt::IReferenced {
	public:
		virtual HRESULT IfaceCalling push_back(IDevice_Event *event) = 0;		
	};

	using TCreate_Filter_Executor = HRESULT(IfaceCalling*)(const GUID *filter_id, IFilter_Executor* next_in_chain);


	//The following GUIDs advertise known filters 
	constexpr GUID Drawing_Filter = { 0x850a122c, 0x8943, 0xa211,{ 0xc5, 0x14, 0x25, 0xba, 0xa9, 0x14, 0x35, 0x74 } };
	constexpr GUID Log_Filter = { 0xc0e942b9, 0x3928, 0x4b81,{ 0x9b, 0x43, 0xa3, 0x47, 0x66, 0x82, 0x0, 0xBA } };
	constexpr GUID Error_Filter = { 0x4a125499, 0x5dc8, 0x128e,{ 0xa5, 0x5c, 0x14, 0x22, 0xbc, 0xac, 0x10, 0x74 } };

	//The following interfaces can be access via refcnt::IUnknown::QueryInterface 

	enum class NError_Marker : size_t {
		Average = 0,
		StdDev,
		AIC,
		Sum,
		count
	};

	enum class NError_Percentile : size_t {
		Minimum = 0,
		P25,
		Median,
		P75,
		P95,
		P99,
		Maximum,
		count
	};

	enum class NError_Range : size_t {
		R5,
		R10,
		R25,
		R50,
		count
	};

	// Structure for containing error metric values
	struct TError_Markers {
		union
		{
			double markers[static_cast<size_t>(NError_Marker::count)];
			struct
			{
				double avg;						// average value
				double stddev;					// standard deviation with Bessel's correction
				double aic;						// Akaike's information criterion
				double sum;						// sum of errors
			};
		};
		union
		{
			double percentile[static_cast<size_t>(NError_Percentile::count)]; // min, q1, median, q3, q95, q99, max
			struct
			{
				double minval;					// minimum value (0. percentile)
				double p25;						// 1. quartile (25. percentile)
				double median;					// median (50. percentile)
				double p75;						// 3. quartile (75. percentile)
				double p95;						// 95. percentile
				double p99;						// 99. percentile
				double maxval;					// maximum value ("100." percentile)
			};
		};
		union
		{
			double range[static_cast<size_t>(NError_Range::count)];
			struct
			{
				double r5;						// 5% range
				double r10;						// 10% range
				double r25;						// 25% range
				double r50;						// 50% range
			};
		};
	};

	// error types
	enum class NError_Type : size_t
	{
		Absolute = 0,
		Relative,
		count
	};

	// supported types of drawing outputs
	enum class TDrawing_Image_Type : size_t
	{
		Graph = 0,
		Day,
		Parkes,
		Clark,
		AGP,
		ECDF,
		Profile_Glucose,
		Profile_Insulin,
		Profile_Carbs,

		count
	};

	// supported diagnosis
	enum class TDiagnosis : size_t
	{
		Type1 = 0,
		Type2,
		Gestational,

		count,
		NotSpecified = Type1
	};

	constexpr GUID Error_Filter_Inspection = { 0x13ebd008, 0x5284, 0x4520,{ 0xbc, 0x2a, 0xa9, 0x18, 0x25, 0x7e, 0x66, 0x8 } };
	class IError_Filter_Inspection : public virtual refcnt::IReferenced {
	public:
		// retrieves error markers for a given signal and error type
		virtual HRESULT IfaceCalling Get_Errors(const GUID *signal_id, const glucose::NError_Type type, glucose::TError_Markers *markers) = 0;
	};

	constexpr GUID Drawing_Filter_Inspection = { 0xd0c81596, 0xdea0, 0x4edf,{ 0x8b, 0x97, 0xe1, 0xd3, 0x78, 0xda, 0xfe, 0x3d } };
	class IDrawing_Filter_Inspection : public virtual refcnt::IReferenced {
	public:
		// retrieves generated SVG for given drawing type and diagnosis
		virtual HRESULT IfaceCalling Draw(TDrawing_Image_Type type, TDiagnosis diagnosis, refcnt::str_container *svg, refcnt::IVector_Container<uint64_t> *segmentIds, refcnt::IVector_Container<GUID> *signalIds) = 0;
	};

	constexpr GUID Log_Filter_Inspection = { 0xa6054c8d, 0x5c01, 0x9e1d,{ 0x14, 0x39, 0x50, 0xda, 0xd1, 0x08, 0xc9, 0x48 } };
	class ILog_Filter_Inspection : public virtual refcnt::IReferenced {
	public:
		/* retrives newly available log records - caller TAKES ownership of the records
		   returns S_OK if there was at least one log record, S_FALSE if none, E_FAIL otherwise
		*/
		virtual HRESULT IfaceCalling Pop(refcnt::wstr_list **str) = 0;
	};


	constexpr GUID Calculate_Filter_Inspection = { 0xec44cd18, 0x8d08, 0x46d1, { 0xa6, 0xcb, 0xc2, 0x43, 0x8e, 0x4, 0x19, 0x88 } };	
	class ICalculate_Filter_Inspection : public virtual refcnt::IReferenced {
	public:
		// makes a deep copy of the entire progress
		virtual HRESULT IfaceCalling Get_Solver_Progress(solver::TSolver_Progress* const progress) = 0;
		// retrieves solver information
		virtual HRESULT IfaceCalling Get_Solver_Information(GUID* const calculated_signal_id, glucose::TSolver_Status* const status) const = 0;
		// explicitly cancels solver
		virtual HRESULT IfaceCalling Cancel_Solver() = 0;
	};


	/* sync and async pipes will vanish by moving filter chain manager into the factory dll*/

	class IFilter_Asynchronous_Pipe : public virtual IEvent_Receiver, public virtual IEvent_Sender {
	public:
		// abort pipe operation explicitly - any subsequent send or receive calls will fail with S_FALSE
		virtual HRESULT IfaceCalling abort() = 0;
	};

	class IFilter_Synchronous_Pipe : public virtual IFilter_Asynchronous_Pipe {
	public:
		virtual HRESULT IfaceCalling add_filter(IFilter* filter) = 0;

		//helper functions that will stop violating the COM rules once the pipe iface vanishes by residing solely in factory dll
		virtual IEvent_Receiver* Get_Reader() = 0;
		virtual IEvent_Sender* Get_Writer() = 0;
	};

	using TCreate_Filter_Asynchronous_Pipe = HRESULT(IfaceCalling *)(glucose::IFilter_Asynchronous_Pipe **pipe);
	using TCreate_Filter_Synchronous_Pipe = HRESULT(IfaceCalling *)(glucose::IFilter_Synchronous_Pipe **pipe);
}
