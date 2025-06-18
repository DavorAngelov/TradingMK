import React from 'react';
import EvaluationSection from './EvaluationSection/EvaluationSection.jsx';
import AllocationSection from './AllocationSection/AllocationSection.jsx';
import BreakdownSection from './BreakdownSection/BreakdownSection.jsx';
import Menu from '../Menu/Menu.jsx'
const Dashboard = () => {
    return (
        <div className="max-w-7xl mx-auto space-y-8 pt-20">
            <Menu/>
            <div className="mb-8">
                <h1 className="text-4xl  text-gray-300 mb-2 font-bold">Dashboard</h1>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <EvaluationSection />
                <AllocationSection />
            </div>

            <BreakdownSection />
        </div>
    );
};

export default Dashboard;