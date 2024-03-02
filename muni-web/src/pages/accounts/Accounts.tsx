import AddIcon from '@mui/icons-material/Add';
import { IconButton, Tooltip, Typography } from "@mui/material";
import { useState } from "react";
import { useCustomerAccounts } from "../../api/bank-account-data-api";
import AccountsList from "../../components/accounts-list/AccountsList";
import BankInstitutionSelection from "../../components/bank-institution-selection/BankInstitutionSelection";
import { useAuth } from "../../contexts/AuthContext";

const Accounts = () => {
    const [wantToAdd, setWantToAdd] = useState(false);
    const { currentUser } = useAuth();
    const { data: accounts } = useCustomerAccounts(currentUser);

    return (
        <div style={{ maxWidth: '1256px', margin: 'auto' }}>
            {wantToAdd && <BankInstitutionSelection onClose={() => setWantToAdd(false)} />}
            {!wantToAdd && (
                <>
                    <div style={{ display: 'flex' }}>
                        <Typography sx={{ flexGrow: 1, marginBottom: '12px' }} variant="h3">Accounts</Typography>

                        <Tooltip title="Connect new account">
                            <IconButton size="large" onClick={() => setWantToAdd(true)}>
                                <AddIcon fontSize="inherit" />
                            </IconButton>
                        </Tooltip>
                    </div>
                    {accounts && <AccountsList accounts={accounts} />}
                </>
            )}

        </div>
    );
};

export default Accounts;