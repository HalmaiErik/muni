import { useCustomerAccounts } from "../../api/bank-account-data-api";
import AccountsList from "../../components/accounts-list/AccountsList";
import { useAuth } from "../../contexts/AuthContext";

const Accounts = () => {
    const { currentUser } = useAuth();
    const { data: accounts } = useCustomerAccounts(currentUser);

    return (
        <div style={{ maxWidth: '1256px', margin: 'auto' }}>
            {accounts && <AccountsList accounts={accounts} />}
        </div>
    );
};

export default Accounts;