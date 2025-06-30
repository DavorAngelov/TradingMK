import React from 'react';
import { TrendingUp } from 'lucide-react';

const EvaluationSection = () => {
    return (
        <div className="bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg shadow-sm">
            <div className="flex flex-col space-y-1.5 p-6">
                <h3 className="text-xl font-semibold text-gray-800">Evaluation</h3>
                <div className="text-sm text-gray-600">Total assets</div>
            </div>
            <div className="p-6 pt-0">
                <div className="space-y-6">
                    <div className="flex items-baseline gap-3">
                        <span className="text-4xl font-bold text-gray-900">72.990 MKD</span>
                        <div className="flex items-center gap-2">
              <span className="bg-green-100 text-green-800 px-2 py-1 rounded text-sm font-medium">
                ↗ 1,9%
              </span>
                        </div>
                    </div>

                    <div className="flex items-center gap-2 text-sm">
                        <span className="text-gray-600">Strong performance</span>
                        <TrendingUp className="w-4 h-4 text-green-500" />
                    </div>

                    <div className="h-32 bg-gray-50 rounded-lg flex items-center justify-center border-2 border-dashed border-gray-200">
                        <span className="text-gray-400">Portfolio Performance Chart</span>
                    </div>


                    <div className="grid grid-cols-2 gap-7 pt-4 border-t border-gray-100">
                        <div>
                            <div className="text-sm text-gray-600">Total profit</div>
                            <div className="font-semibold text-green-600">+7.200 MKD</div>
                            <div className="text-xs text-green-500">+15,81%</div>
                        </div>

                        <div>
                            <div className="text-sm text-gray-600">Best-profit stock</div>
                            <div className="font-semibold text-gray-900">Alkaloid</div>
                            <div className="text-xs text-gray-500">ALK</div>
                        </div>
                    </div>


                    <div className="grid grid-cols-1 gap-4 pt-4 border-t border-gray-100">
                        <div className="text-center">
                            <div className="text-sm text-gray-600 mb-2">Portfolio score</div>
                            <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-1">
                                <span className="text-green-800 font-bold">B</span>
                            </div>
                            <div className="font-bold text-2xl">69</div>
                            <div className="text-sm text-gray-500">/100</div>
                            <div className="text-xs text-green-600">Good</div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    );
};

export default EvaluationSection;