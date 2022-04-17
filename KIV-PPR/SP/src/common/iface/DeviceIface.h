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

#include "../rtl/guid.h"
#include "referencedIface.h"

#include <limits>
#include <array>

#undef max

namespace glucose {

	//constant to convert mg/dl to mmol/l
	extern const double mgdl_2_mmoll;	//we assume mmol/l, so to make a conversion possible

	extern const double One_Hour;
	extern const double One_Minute;
	extern const double One_Second;

	const size_t apxNo_Derivation = 0;
	const size_t apxFirst_Order_Derivation = 1;

	/*
		The time is encoded as the number of days since January 0, 1900 00:00 UTC, see
		http://en.wikipedia.org/wiki/January_0

		Integral part stores the number of days, fractional part stores the time.
		It could have been any fixed dates, but this one is compatible with
		FreePascal, Delphi and Microsoft Products such as Excel, Access and COM's variant in general.

		Therefore, 01 Jan 1900 00:00 would be 1.0 and 01 Jan 1900 24:00 would be 2.0

		However, the UI is supposed to use QDateTime whose epoch starts on 1.1. 1970 0:0 UTC0 aka UNIX epoch start.
		But note that leap seconds are not calculated with when using the UNIX epoch!
	*/

	// special GUID value that means "all signal ids" - it is used especially in modules, which are able to work with a set of signals
	constexpr GUID signal_All = { 0xffffffff, 0xffff, 0xffff, { 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff } };	// {FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF}

	constexpr GUID signal_BG = { 0xf666f6c2, 0xd7c0, 0x43e8,{ 0x8e, 0xe1, 0xc8, 0xca, 0xa8, 0xf8, 0x60, 0xe5 } };	// {F666F6C2-D7C0-43E8-8EE1-C8CAA8F860E5}
	constexpr GUID signal_IG = { 0x3034568d, 0xf498, 0x455b,{ 0xac, 0x6a, 0xbc, 0xf3, 0x1, 0xf6, 0x9c, 0x9e } };		// {3034568D-F498-455B-AC6A-BCF301F69C9E}
	constexpr GUID signal_ISIG = { 0x3f62c28a, 0x4d25, 0x4086,{ 0xbd, 0x1a, 0xfc, 0x44, 0x2f, 0xdd, 0xb7, 0xcf } };		// {3F62C28A-4D25-4086-BD1A-FC442FDDB7CF}
	constexpr GUID signal_Calibration = { 0xed4cd0f5, 0xf728, 0x44fe,{ 0x95, 0x52, 0x97, 0x33, 0x8b, 0xd7, 0xe8, 0xd5 } };	// {ED4CD0F5-F728-44FE-9552-97338BD7E8D5}
	constexpr GUID signal_Bolus_Insulin = { 0x22d87566, 0xaf1b, 0x4cc7,{ 0x8d, 0x11, 0xc5, 0xe0, 0x4e, 0x1e, 0x9c, 0x8a } }; 	 // {22D87566-AF1B-4CC7-8D11-C5E04E1E9C8A}
	constexpr GUID signal_Basal_Insulin = { 0xbf88a8cb, 0x1290, 0x4477, { 0xa2, 0xcf, 0xbd, 0xd0, 0x6d, 0xf6, 0x28, 0xab } }; 	 // {BF88A8CB-1290-4477-A2CF-BDD06DF628AB}
	constexpr GUID signal_Basal_Insulin_Rate = { 0xb5897bbd, 0x1e32, 0x408a, { 0xa0, 0xd5, 0xc5, 0xbf, 0xec, 0xf4, 0x47, 0xd9 } };	// {B5897BBD-1E32-408A-A0D5-C5BFECF447D9}
	constexpr GUID signal_Insulin_Activity = { 0xdd057b62, 0xcbd9, 0x45e2, { 0xb1, 0x2, 0xde, 0x94, 0x10, 0x49, 0xa5, 0x5a } };	 // {DD057B62-CBD9-45E2-B102-DE941049A55A}
	constexpr GUID signal_IOB = { 0x313a1c11, 0x6bac, 0x46e2, { 0x89, 0x38, 0x73, 0x53, 0x40, 0x9f, 0x2f, 0xcd } };			// {313A1C11-6BAC-46E2-8938-7353409F2FCD}
	constexpr GUID signal_COB = { 0xb74aa581, 0x538c, 0x4b30, { 0xb7, 0x64, 0x5b, 0xd0, 0xd9, 0x7b, 0x7, 0x27 } };			// {B74AA581-538C-4B30-B764-5BD0D97B0727}
	constexpr GUID signal_Carb_Intake = { 0x37aa6ac1, 0x6984, 0x4a06,{ 0x92, 0xcc, 0xa6, 0x60, 0x11, 0xd, 0xd, 0xc7 } };	// {37AA6AC1-6984-4A06-92CC-A660110D0DC7}
	constexpr GUID signal_Physical_Activity = { 0xf4438e9a, 0xdd52, 0x45bd,{ 0x83, 0xce, 0x5e, 0x93, 0x61, 0x5e, 0x62, 0xbd } }; // {F4438E9A-DD52-45BD-83CE-5E93615E62BD}

	//known calculated signals to allow optimizations (and localizations)
	constexpr GUID signal_Diffusion_v2_Blood = { 0xd96a559b, 0xe247, 0x41e0,{ 0xbd, 0x8e, 0x78, 0x8d, 0x20, 0xdb, 0x9a, 0x70 } }; // {D96A559B-E247-41E0-BD8E-788D20DB9A70}
	constexpr GUID signal_Diffusion_v2_Ist = { 0x870ddbd6, 0xdaf1, 0x4877,{ 0xa8, 0x9e, 0x5e, 0x7b, 0x2, 0x8d, 0xa6, 0xc7 } };  // {870DDBD6-DAF1-4877-A89E-5E7B028DA6C7}
	constexpr GUID signal_Steil_Rebrin_Blood = { 0x287b9bb8, 0xb73b, 0x4485,{ 0xbe, 0x20, 0x2c, 0x8c, 0x40, 0x98, 0x3b, 0x16 } }; // {287B9BB8-B73B-4485-BE20-2C8C40983B16}
	constexpr GUID signal_Steil_Rebrin_Diffusion_Prediction = { 0xf997edf4, 0x357c, 0x4cb2, { 0x8d, 0x7c, 0xf6, 0x81, 0xa3, 0x76, 0xe1, 0x7c } };
	constexpr GUID signal_Diffusion_Prediction = { 0x43fcd03d, 0xb8bc, 0x497d, { 0x9e, 0xab, 0x3d, 0x1e, 0xeb, 0x3e, 0xbb, 0x5c } };

	constexpr GUID signal_oref0_Insulin_Activity_Bilinear = { 0x235962db, 0x1356, 0x4d14, { 0xb8, 0x54, 0x49, 0x3f, 0xd9, 0x79, 0x1, 0x42 } };		// {235962DB-1356-4D14-B854-493FD9790142}
	constexpr GUID signal_oref0_Insulin_Activity_Exponential = { 0x19974ff5, 0x9eb1, 0x4a99, { 0xb7, 0xf2, 0xdc, 0x9b, 0xfa, 0xe, 0x31, 0x5e } };	// {19974FF5-9EB1-4A99-B7F2-DC9BFA0E315E}
	constexpr GUID signal_oref0_IOB_Bilinear = { 0xa8fa0190, 0x4c89, 0x4e0b, { 0x88, 0x55, 0xaf, 0x47, 0xba, 0x29, 0x4c, 0xff } };		// {A8FA0190-4C89-4E0B-8855-AF47BA294CFF}
	constexpr GUID signal_oref0_IOB_Exponential = { 0x238d2353, 0x6d37, 0x402c, { 0xaf, 0x39, 0x6c, 0x55, 0x52, 0xa7, 0x7e, 0x1f } };	// {238D2353-6D37-402C-AF39-6C5552A77E1F}
	constexpr GUID signal_oref0_COB_Bilinear = { 0xe29a9d38, 0x551e, 0x4f3f, { 0xa9, 0x1d, 0x1f, 0x14, 0xd9, 0x34, 0x67, 0xe3 } };		// {E29A9D38-551E-4F3F-A91D-1F14D93467E3}
	constexpr GUID signal_oref0_BG_IOB_Prediction = { 0x671b6b9, 0x9563, 0x4b06, { 0xa5, 0xf0, 0xe6, 0xdb, 0x4e, 0x35, 0xa9, 0x64 } };	// {0671B6B9-9563-4B06-A5F0-E6DB4E35A964}
	constexpr GUID signal_oref0_BG_COB_Prediction = { 0xbb7251d7, 0x474b, 0x4e3d, { 0x87, 0x41, 0x2, 0xda, 0x6f, 0x60, 0x39, 0xf9 } };	// {BB7251D7-474B-4E3D-8741-02DA6F6039F9}
	constexpr GUID signal_oref0_BG_UAM_Prediction = { 0x10eca207, 0xda9b, 0x4794, { 0xb2, 0xea, 0x8c, 0x65, 0xb, 0xd7, 0xb3, 0x91 } };	// {10ECA207-DA9B-4794-B2EA-8C650BD7B391}
	constexpr GUID signal_oref0_BG_ZT_Prediction = { 0xa272d443, 0xbeee, 0x4798, { 0xb4, 0x93, 0xa8, 0x8d, 0x20, 0x9e, 0x5b, 0x80 } };	// {A272D443-BEEE-4798-B493-A88D209E5B80}
	constexpr GUID signal_oref0_BG_Prediction = { 0xa62ee3b9, 0x41e5, 0x470a, { 0x8a, 0x8b, 0x8, 0x73, 0x73, 0x77, 0x9c, 0xb4 } };		// {A62EE3B9-41E5-470A-8A8B-087373779CB4}
	constexpr GUID signal_oref0_Basal_Insulin_Rate = { 0xcce6d481, 0x82f6, 0x42f0, { 0xb6, 0x4d, 0x99, 0xd1, 0x72, 0xc, 0xe4, 0x83 } };		// {CCE6D481-82F6-42F0-B64D-99D1720CE483}
	constexpr GUID signal_oref0_Basal_Insulin_Rate_Orig = { 0xbea3d646, 0x543a, 0x482d, { 0x9b, 0xc5, 0x73, 0xa4, 0x51, 0xb, 0xd1, 0x91 } };	// {BEA3D646-543A-482D-9BC5-73A4510BD191}

	constexpr GUID signal_Calculated_Bolus_Insulin = { 0x9b16b4a, 0x54c2, 0x4c6a, { 0x94, 0x8a, 0x3d, 0xef, 0x85, 0x33, 0x5, 0x9b } };		// {09B16B4A-54C2-4C6A-948A-3DEF8533059B}

	constexpr GUID signal_Constant_BG = { 0x203f7b77, 0x839, 0x4382, { 0x90, 0xb1, 0x33, 0x92, 0xa1, 0xdf, 0x5c, 0x16 } };			// {203F7B77-0839-4382-90B1-3392A1DF5C16}
	constexpr GUID signal_Constant_Insulin = { 0xc7f92a96, 0x1fa3, 0x42a2, { 0xb8, 0x25, 0x35, 0x2f, 0xa, 0x41, 0x70, 0x2d } };		// {C7F92A96-1FA3-42A2-B825-352F0A41702D}

	//virtual signals used for temporal remapping
	extern const std::array<GUID, 100> signal_Virtual;

	using IModel_Parameter_Vector = refcnt::double_container;

	enum class NDevice_Event_Code : uint8_t {
		Nothing = 0,		//internal event of the object
		Shut_Down,

		Level,				//level contains newly measured or calculated level of a given signal
		Masked_Level,		//level not advertised for solving, i.e., tranining, but for testing set
		Parameters,			//new parameters are available for a given signal
		Parameters_Hint,	//some solver requires e.g., initial estimate of the model parameters so that
							//params stored in non-volatile memory should be broadcasted so that
							//solvers and calculators can use them

		//-------- simulation related codes ------
		Suspend_Parameter_Solving,
		Resume_Parameter_Solving,
		Solve_Parameters,	//user can either request to recalculate, or we can request to recalculate it at the end of the segment - i.e., prior sending Time_Segment_Stop
		Time_Segment_Start,
		Time_Segment_Stop,
		Warm_Reset,			//all incoming levels (and associated errors) are thrown away, calculated parameters are kept and DB/File input filters replays the data from begining, while CGMS input filter just ignores this message
		Synchronization,	//to synchronize feedback filters; info holds feedback filter identifier, if used within feedback loop


		//-------- codes intended for log parsers ------
		Information,
		Warning,
		Error,

		count
	};


	struct TDevice_Event {
		NDevice_Event_Code event_code;

		GUID device_id;					//supporting parallel measurements
		GUID signal_id;					//blood, ist, isig, model id aka e.g, calculated blood, etc.

		double device_time;				//signal with multiple values are aggregated byt device_time with the same signal_id and device_id
		int64_t logical_time;

		uint64_t segment_id;			// segment identifier or Invalid_Segment_Id

		union {
			double level;
			IModel_Parameter_Vector* parameters;		//this will have to be marshalled
														//as different models have different number of parameters, statically sized field would case over-complicated code later on
			refcnt::wstr_container* info;				//information, warning, error 
		};
	};

	//To make TDevice_Event handling more efficient, particulalry when passing through the pipe,
	//IDevice_Event exposes TDevice_Event container iface so that the pipe can accept and pass throught only a pointer, not the entire structure.
	//This way, we avoid the overhead of copying size_of(TDevice_Event) so many times.
	class IDevice_Event : public virtual refcnt::IUnique_Reference {
	public:
		// provides pointer to the contained TDevice_Event (free to modify as needed)
		virtual HRESULT IfaceCalling Raw(TDevice_Event **raw) = 0;
	};

	static constexpr decltype(TDevice_Event::segment_id) Invalid_Segment_Id = std::numeric_limits<decltype(Invalid_Segment_Id)>::max();
	static constexpr decltype(TDevice_Event::segment_id) All_Segments_Id = std::numeric_limits<decltype(All_Segments_Id)>::max() - 1;

	typedef struct {
		double Min, Max;
	} TBounds;


	class ISignal : public virtual refcnt::IReferenced {
	public:
		/* on S_OK, *filled elements were copied into times and double levels of the count size
		   for measured signal, it returns the measured values
		   for calculated signal, it returns E_NOIMPL
		*/
		virtual HRESULT IfaceCalling Get_Discrete_Levels(double* const times, double* const levels, const size_t count, size_t *filled) const = 0;

		/* gets bounds and level_count, any of these parameters can be nullptr
		   for measured and calculated signals, dtto Get_Discrete_Levels
		*/
		virtual HRESULT IfaceCalling Get_Discrete_Bounds(TBounds* const time_bounds, TBounds* const level_bounds, size_t *level_count) const = 0;

		/* adds measured levels to internal containers
		   for measured and calculated signals, dtto Get_Discrete_Levels
		*/
		virtual HRESULT IfaceCalling Add_Levels(const double *times, const double *levels, const size_t count) = 0;

		/*
			this method will be called in parallel by solvers and therefore it has to be const

			params - params from which to calculate the signal
						can be nullptr to indicate use of default parameters
			times - times at which to get the levels, i.e., y values for x values
			levels - the levels, must be already allocated with size of count
					- level that cannot be calculated must be se to quiet nan
			count - the total number of times for which to get the levels
			derivation_order - order of derivation requested
		*/
		virtual HRESULT IfaceCalling Get_Continuous_Levels(IModel_Parameter_Vector *params,
			const double* times, double* const levels, const size_t count, const size_t derivation_order) const = 0;

		// returns default parameters on calculated signals, E_NOIMPL on measured signal
		virtual HRESULT IfaceCalling Get_Default_Parameters(IModel_Parameter_Vector *parameters) const = 0;
	};


	class ITime_Segment : public virtual refcnt::IReferenced {
	public:
		// retrieves or creates signal with given id; calls AddRef on returned object
		virtual HRESULT IfaceCalling Get_Signal(const GUID *signal_id, ISignal **signal) = 0;
	};

	// segment provides source levels for the calculation
	// only ITime_Segment::Get_Signal is supposed to call this function to avoid (although not probihit) creating of over-complex segment-graphs
	using TCreate_Signal = HRESULT(IfaceCalling *)(const GUID *calc_id, ITime_Segment *segment, ISignal **signal);

	using TCreate_Device_Event = HRESULT(IfaceCalling *)(glucose::NDevice_Event_Code code, glucose::IDevice_Event **event);
}
